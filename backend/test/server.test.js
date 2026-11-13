'use strict';

const test = require('node:test');
const assert = require('node:assert');
const { createServer } = require('../src/server');
const { loadConfig } = require('../src/config');
const { selectProvider } = require('../src/providers');

function start(configOverrides = {}, provider = null) {
  const config = { ...loadConfig({}), ...configOverrides };
  const server = createServer(config, provider);
  return new Promise((resolve) => {
    server.listen(0, '127.0.0.1', () => {
      resolve({ server, base: `http://127.0.0.1:${server.address().port}` });
    });
  });
}

function stop(server) {
  server.closeAllConnections();
  server.close();
}

const post = (base, body, headers = { 'content-type': 'application/json' }) =>
  fetch(`${base}/v1/chat`, {
    method: 'POST',
    headers,
    body: typeof body === 'string' ? body : JSON.stringify(body),
  });

const good = { language: 'en', messages: [{ role: 'user', content: 'What is Tawaf?' }] };

test('health works and reports demo mode', async () => {
  const { server, base } = await start();
  const r = await fetch(`${base}/health`);
  assert.strictEqual(r.status, 200);
  assert.deepStrictEqual(await r.json(), { status: 'ok', mode: 'demo' });
  stop(server);
});

test('demo mode answers a valid request in all languages', async () => {
  const { server, base } = await start();
  for (const language of ['bn', 'en', 'ar']) {
    const r = await post(base, { ...good, language });
    assert.strictEqual(r.status, 200);
    const j = await r.json();
    assert.strictEqual(j.mode, 'demo');
    assert.ok(j.reply.length > 10);
  }
  stop(server);
});

test('rejects invalid input', async () => {
  const { server, base } = await start();
  assert.strictEqual((await post(base, '{oops')).status, 400);
  assert.strictEqual((await post(base, { language: 'fr', messages: good.messages })).status, 400);
  assert.strictEqual((await post(base, { messages: [] })).status, 400);
  assert.strictEqual((await post(base, { messages: [{ role: 'system', content: 'x' }] })).status, 400);
  assert.strictEqual((await post(base, { messages: [{ role: 'assistant', content: 'x' }] })).status, 400);
  assert.strictEqual((await post(base, { messages: [{ role: 'user', content: 'a'.repeat(2001) }] })).status, 400);
  assert.strictEqual((await post(base, good, { 'content-type': 'text/plain' })).status, 415);
  stop(server);
});

test('rejects oversized bodies', async () => {
  const { server, base } = await start();
  const r = await post(base, 'x'.repeat(40 * 1024));
  assert.strictEqual(r.status, 413);
  stop(server);
});

test('rate limiting returns 429 with Retry-After', async () => {
  const { server, base } = await start({ rateLimitPerMin: 2 });
  assert.strictEqual((await post(base, good)).status, 200);
  assert.strictEqual((await post(base, good)).status, 200);
  const r = await post(base, good);
  assert.strictEqual(r.status, 429);
  assert.ok(r.headers.get('retry-after'));
  stop(server);
});

test('provider failure becomes a clean 502 without leaking details', async () => {
  const failing = { name: 'fake', complete: async () => { throw new Error('secret-detail'); } };
  const { server, base } = await start({}, failing);
  const r = await post(base, good);
  assert.strictEqual(r.status, 502);
  const text = await r.text();
  assert.ok(!text.includes('secret-detail'));
  stop(server);
});

test('unknown routes and wrong methods', async () => {
  const { server, base } = await start();
  assert.strictEqual((await fetch(`${base}/nope`)).status, 404);
  assert.strictEqual((await fetch(`${base}/v1/chat`)).status, 405);
  stop(server);
});

test('missing API key falls back to demo provider', () => {
  const quiet = { warn() {} };
  const cfg = loadConfig({ AI_PROVIDER: 'anthropic' });
  assert.strictEqual(selectProvider(cfg, quiet).name, 'demo');
  const cfg2 = loadConfig({ AI_PROVIDER: 'anthropic', ANTHROPIC_API_KEY: 'k' });
  assert.strictEqual(selectProvider(cfg2, quiet).name, 'anthropic');
  const cfg3 = loadConfig({ AI_PROVIDER: 'openai', OPENAI_API_KEY: 'k' });
  assert.strictEqual(selectProvider(cfg3, quiet).name, 'demo'); // model missing
});
