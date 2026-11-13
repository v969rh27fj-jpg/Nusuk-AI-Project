'use strict';

const { buildSystemPrompt } = require('../prompt');

/** Works with OpenAI and any API that follows the same /chat/completions format. */
function create(config) {
  return {
    name: 'openai',
    async complete({ messages, language, signal }) {
      const res = await fetch(`${config.openaiBaseUrl}/chat/completions`, {
        method: 'POST',
        signal,
        headers: {
          'content-type': 'application/json',
          authorization: `Bearer ${config.openaiKey}`,
        },
        body: JSON.stringify({
          model: config.openaiModel,
          max_tokens: 900,
          messages: [{ role: 'system', content: buildSystemPrompt(language) }, ...messages],
        }),
      });
      if (!res.ok) throw new Error(`openai_http_${res.status}`);
      const data = await res.json();
      const text = data.choices && data.choices[0] && data.choices[0].message
        ? String(data.choices[0].message.content || '').trim()
        : '';
      if (!text) throw new Error('openai_empty');
      return text;
    },
  };
}

module.exports = { create };
