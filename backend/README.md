# Nusuk AI backend

Small HTTPS-ready API that sits between the Android app and an AI provider. **Zero npm dependencies** (Node 18+), so there is nothing to install.

## Endpoints
- `GET /health` → `{"status":"ok","mode":"demo"|"live"}`
- `POST /v1/chat` body: `{"language":"bn|en|ar","messages":[{"role":"user|assistant","content":"..."}]}` (1–20 messages, ≤2000 chars each, last one from `user`) → `{"reply":"...","mode":"demo|live"}`
- Errors: `400` invalid, `413` too large, `415` not JSON, `429` rate limited (with `Retry-After`), `502` provider unavailable.

## Run locally
```
cd backend
cp .env.example .env        # then edit values (Windows: copy .env.example .env)
node --env-file=.env src/server.js    # Node 20.6+ ; or set variables in your shell
npm test                    # 8 tests
```
With `AI_PROVIDER=demo` (default) it works with no key and answers with a "demo mode" notice.

## Real AI
Set on your host (never in code, never in git):
```
AI_PROVIDER=anthropic
ANTHROPIC_API_KEY=...        # secret
ANTHROPIC_MODEL=claude-sonnet-5-5
```
or `AI_PROVIDER=openai` with `OPENAI_API_KEY`, `OPENAI_MODEL` (and `OPENAI_BASE_URL` for any OpenAI-compatible service). If a key is missing the server falls back to demo mode and logs a warning.

### Switching provider later
Providers live in `src/providers/`. Each exports `{ name, complete({messages, language, signal}) }`. Add a file, register it in `providers/index.js`. The Android app does not change.

## HTTPS
Deploy on a host that terminates TLS for you (Render, Railway, Fly.io, Cloud Run …) and use its `https://` address in the app. Set `TRUST_PROXY=true` there so rate limiting sees real client IPs. A `Dockerfile` is included.

## Hardening notes
Rate limit is per instance, in memory (use Redis if you scale to many instances). Set a monthly spending cap at your AI provider. The system prompt (`src/prompt.js`) tells the model not to issue fatwas and to recommend a scholar when unsure.
