'use strict';

const { buildSystemPrompt } = require('../prompt');

function create(config) {
  return {
    name: 'anthropic',
    async complete({ messages, language, signal }) {
      const res = await fetch('https://api.anthropic.com/v1/messages', {
        method: 'POST',
        signal,
        headers: {
          'content-type': 'application/json',
          'x-api-key': config.anthropicKey,
          'anthropic-version': '2023-06-01',
        },
        body: JSON.stringify({
          model: config.anthropicModel,
          max_tokens: 900,
          system: buildSystemPrompt(language),
          messages,
        }),
      });
      if (!res.ok) throw new Error(`anthropic_http_${res.status}`);
      const data = await res.json();
      const text = (data.content || [])
        .filter((b) => b.type === 'text')
        .map((b) => b.text)
        .join('\n')
        .trim();
      if (!text) throw new Error('anthropic_empty');
      return text;
    },
  };
}

module.exports = { create };
