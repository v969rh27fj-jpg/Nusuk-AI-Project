'use strict';

const demo = require('./demo');
const anthropic = require('./anthropic');
const openai = require('./openai');

/**
 * Provider abstraction: every provider exposes
 *   { name, complete({messages, language, signal}) -> Promise<string> }
 * To add a new provider, create a file like the ones above and register it here.
 * The Android app never changes, because it only talks to POST /v1/chat.
 */
function selectProvider(config, log = console) {
  if (config.provider === 'anthropic') {
    if (config.anthropicKey) return anthropic.create(config);
    log.warn('AI_PROVIDER=anthropic but ANTHROPIC_API_KEY is missing. Falling back to demo mode.');
  } else if (config.provider === 'openai') {
    if (config.openaiKey && config.openaiModel) return openai.create(config);
    log.warn('AI_PROVIDER=openai needs OPENAI_API_KEY and OPENAI_MODEL. Falling back to demo mode.');
  } else if (config.provider !== 'demo') {
    log.warn(`Unknown AI_PROVIDER "${config.provider}". Falling back to demo mode.`);
  }
  return demo;
}

module.exports = { selectProvider };
