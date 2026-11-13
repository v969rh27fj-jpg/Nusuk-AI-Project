'use strict';

/** Reads configuration from environment variables only. No secrets live in code. */
function loadConfig(env = process.env) {
  const provider = (env.AI_PROVIDER || 'demo').toLowerCase();
  const num = (v, d) => {
    const n = parseInt(v, 10);
    return Number.isFinite(n) && n > 0 ? n : d;
  };
  return {
    port: num(env.PORT, 8080),
    provider,
    anthropicKey: env.ANTHROPIC_API_KEY || '',
    anthropicModel: env.ANTHROPIC_MODEL || 'claude-sonnet-5-5',
    openaiKey: env.OPENAI_API_KEY || '',
    openaiBaseUrl: (env.OPENAI_BASE_URL || 'https://api.openai.com/v1').replace(/\/+$/, ''),
    openaiModel: env.OPENAI_MODEL || '',
    rateLimitPerMin: num(env.RATE_LIMIT_PER_MIN, 20),
    trustProxy: String(env.TRUST_PROXY || '').toLowerCase() === 'true',
    maxBodyBytes: 32 * 1024,
    requestTimeoutMs: 30000,
  };
}

module.exports = { loadConfig };
