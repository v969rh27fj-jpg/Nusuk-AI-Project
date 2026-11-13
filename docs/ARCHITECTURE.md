# Architecture

```
Android app (Kotlin, MVVM)                         Your backend (Node)         AI provider
┌──────────────────────────────┐   HTTPS POST      ┌───────────────────┐       ┌───────────┐
│ Fragment (ViewBinding)       │  /v1/chat         │ validate + limit  │  key  │ Claude /  │
│   ↕ StateFlow                │ ───────────────►  │ providers/*       │ ────► │ OpenAI /… │
│ ViewModel                    │ ◄───────────────  │ (demo/anthropic/  │       └───────────┘
│   ↕ suspend                  │   {reply}         │  openai)          │   key lives ONLY here
│ Repository (Chat/Quiz/Content)│                  └───────────────────┘
│   ├─ ChatApi (HttpURLConnection, https only)
│   ├─ DemoAnswerEngine (offline, no AI)
│   ├─ ContentRepository (assets JSON, offline)
│   └─ NusukDb (SQLite) + SettingsStore (SharedPreferences)
└──────────────────────────────┘
```

- **MVVM**: `ChatViewModel` (fragment-scoped), `QuizViewModel` (activity-scoped so a running quiz survives tab changes).
- **No third-party libraries** beyond AndroidX, Material, coroutines. Networking uses `HttpURLConnection` + `org.json`; storage uses `SQLiteOpenHelper` — fewer moving parts for a project built without a local compiler.
- **Database** (`nusuk.db`): `chat_messages`, `quiz_results`, `topic_progress`. Preferences (language, theme, font, switches, name, avatar path, backend URL) in SharedPreferences.
- **Localization**: `values/` = Bengali (default), `values-en`, `values-ar` generated from one table so keys always match. Language applied with `AppCompatDelegate.setApplicationLocales`; Arabic flips layouts to RTL (`supportsRtl`, start/end attributes). Learning content and quiz JSON hold all three languages.
- **Not implemented (honest list)**: quiz pause/resume across app restarts, Room, Hilt, instrumented tests, release signing, remote content download (content is bundled so it works offline).
