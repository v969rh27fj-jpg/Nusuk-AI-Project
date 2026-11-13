'use strict';

const http = require('http');
const { loadConfig } = require('./config');
const { validateChatRequest } = require('./validate');
const { createRateLimiter } = require('./rateLimit');
const { selectProvider } = require('./providers');

const SECURITY_HEADERS = {
  'content-type': 'application/json; charset=utf-8',
  'x-content-type-options': 'nosniff',
  'cache-control': 'no-store',
  'strict-transport-security': 'max-age=31536000; includeSubDomains',
};

function send(res, status, payload, extra = {}) {
  res.writeHead(status, { ...SECURITY_HEADERS, ...extra });
  res.end(JSON.stringify(payload));
}

function readJson(req, maxBytes) {
  return new Promise((resolve, reject) => {
    let size = 0;
    let failed = false;
    const chunks = [];
    req.on('data', (chunk) => {
      if (failed) return;
      size += chunk.length;
      if (size > maxBytes) {
        failed = true;
        chunks.length = 0;
        reject(Object.assign(new Error('too_large'), { status: 413 }));
        return;
      }
      chunks.push(chunk);
    });
    req.on('end', () => {
      if (failed) return;
      try {
        resolve(JSON.parse(Buffer.concat(chunks).toString('utf8')));
      } catch {
        reject(Object.assign(new Error('bad_json'), { status: 400 }));
      }
    });
    req.on('error', (e) => {
      if (!failed) reject(e);
    });
  });
}

function clientIp(req, trustProxy) {
  if (trustProxy) {
    const fwd = req.headers['x-forwarded-for'];
    if (typeof fwd === 'string' && fwd.length > 0) return fwd.split(',')[0].trim();
  }
  return req.socket.remoteAddress || 'unknown';
}

/** Builds the HTTP server. `providerOverride` is used by tests. */
function createServer(config = loadConfig(), providerOverride = null) {
  const provider = providerOverride || selectProvider(config);
  const limiter = createRateLimiter({ limit: config.rateLimitPerMin });

  const server = http.createServer(async (req, res) => {
    try {
      const url = new URL(req.url, 'http://localhost');

      if (req.method === 'GET' && url.pathname === '/health') {
        return send(res, 200, { status: 'ok', mode: provider.name === 'demo' ? 'demo' : 'live' });
      }

      if (url.pathname === '/v1/chat') {
        if (req.method !== 'POST') return send(res, 405, { error: 'method_not_allowed' }, { allow: 'POST' });

        const rl = limiter.check(clientIp(req, config.trustProxy));
        if (!rl.allowed) {
          return send(res, 429, { error: 'rate_limited' }, { 'retry-after': String(rl.retryAfterSec) });
        }

        const type = String(req.headers['content-type'] || '');
        if (!type.toLowerCase().startsWith('application/json')) {
          return send(res, 415, { error: 'content_type_must_be_json' });
        }

        let body;
        try {
          body = await readJson(req, config.maxBodyBytes);
        } catch (e) {
          if (e.message === 'too_large') {
            // Answer first, then drop the connection so the rest of the upload is ignored.
            res.on('finish', () => req.destroy());
            return send(res, 413, { error: 'payload_too_large' }, { connection: 'close' });
          }
          return send(res, e.status || 400, { error: 'invalid_json' });
        }

        const v = validateChatRequest(body);
        if (!v.ok) return send(res, 400, { error: 'invalid_request', detail: v.error });

        const controller = new AbortController();
        const timer = setTimeout(() => controller.abort(), config.requestTimeoutMs);
        try {
          const reply = await provider.complete({ ...v.value, signal: controller.signal });
          return send(res, 200, { reply, mode: provider.name === 'demo' ? 'demo' : 'live' });
        } catch (err) {
          // Log only the error code, never user content or keys.
          console.error('provider_error:', err && err.message ? err.message : 'unknown');
          return send(res, 502, { error: 'ai_provider_unavailable' });
        } finally {
          clearTimeout(timer);
        }
      }

      return send(res, 404, { error: 'not_found' });
    } catch (err) {
      console.error('server_error:', err && err.message ? err.message : 'unknown');
      if (!res.headersSent) send(res, 500, { error: 'internal_error' });
    }
  });

  server.on('close', () => limiter.stop());
  return server;
}

if (require.main === module) {
  const config = loadConfig();
  const server = createServer(config);
  server.listen(config.port, () => {
    console.log(`Nusuk AI backend listening on port ${config.port} (provider: ${config.provider})`);
    console.log('Serve this behind HTTPS (your host or reverse proxy must provide TLS).');
  });
}

module.exports = { createServer };
