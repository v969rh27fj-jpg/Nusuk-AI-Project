'use strict';

const LANGUAGES = ['bn', 'en', 'ar'];
const MAX_MESSAGES = 20;
const MAX_MESSAGE_CHARS = 2000;
const MAX_TOTAL_CHARS = 12000;

/** Returns { ok: true, value } or { ok: false, error }. */
function validateChatRequest(body) {
  if (!body || typeof body !== 'object' || Array.isArray(body)) {
    return { ok: false, error: 'Body must be a JSON object.' };
  }
  const language = body.language === undefined ? 'bn' : body.language;
  if (!LANGUAGES.includes(language)) {
    return { ok: false, error: 'language must be one of: bn, en, ar.' };
  }
  const { messages } = body;
  if (!Array.isArray(messages) || messages.length === 0 || messages.length > MAX_MESSAGES) {
    return { ok: false, error: `messages must be an array of 1-${MAX_MESSAGES} items.` };
  }
  let total = 0;
  const clean = [];
  for (const m of messages) {
    if (!m || typeof m !== 'object') return { ok: false, error: 'Invalid message.' };
    if (m.role !== 'user' && m.role !== 'assistant') {
      return { ok: false, error: 'role must be "user" or "assistant".' };
    }
    if (typeof m.content !== 'string') return { ok: false, error: 'content must be a string.' };
    const content = m.content.trim();
    if (content.length === 0 || content.length > MAX_MESSAGE_CHARS) {
      return { ok: false, error: `content must be 1-${MAX_MESSAGE_CHARS} characters.` };
    }
    total += content.length;
    clean.push({ role: m.role, content });
  }
  if (total > MAX_TOTAL_CHARS) return { ok: false, error: 'Conversation is too long.' };
  if (clean[clean.length - 1].role !== 'user') {
    return { ok: false, error: 'The last message must be from the user.' };
  }
  return { ok: true, value: { messages: clean, language } };
}

module.exports = { validateChatRequest, LANGUAGES };
