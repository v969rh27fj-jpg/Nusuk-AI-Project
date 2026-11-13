# Nusuk AI (নুসুক এআই) — Hajj & Umrah learning app

> **Independent educational app.** It is **not** the official Saudi "Nusuk" app and has no affiliation with any government body. It does not issue fatwas.

Native Android (Kotlin, MVVM, Material 3) · Bengali (default) / English / Arabic (RTL) · offline learning content and quiz · optional AI assistant through **your own secure backend** · optional floating AI button.

## ⚠️ Honest status (read this first)

| Item | Status |
|---|---|
| Backend (`backend/`) | ✅ **Tested**: 8 automated tests pass (`npm test`) |
| JSON content, XML resources, manifest | ✅ Checked by script (well-formed, all `@string/@id/@drawable` references resolve, 3 languages have identical keys) |
| Kotlin code + Gradle build + APK | ⚠️ **Not compiled yet.** The environment where this project was written had no Android SDK, no Gradle and no internet. The first GitHub Actions run is the real build test. If it fails, copy the error from the log and fix it (usually a one-line issue). |
| APK | ❌ Not produced here. GitHub Actions produces it (see below). |
| Question bank | ⚠️ **22 starter questions**, not reviewed by a scholar. 100/200/300 sessions are disabled until the bank is large enough. |
| Learning content | ⚠️ Short educational summaries with references. Have a qualified scholar review before public release. |

## Quick start — build the APK in the cloud (no Android Studio)

1. Create a free account at <https://github.com>, then **New repository** (e.g. `nusuk-ai`).
2. Upload **everything** from this folder (keep the folder structure, including the hidden `.github` folder).
3. Open the **Actions** tab → *Build Nusuk AI APK* → wait for the green tick.
   - If it is red, open the failed step and read the log.
4. Open the finished run → **Artifacts** → download `Nusuk-AI-debug-apk` → unzip → `app-debug.apk`.
5. Copy the APK to your phone and install it (allow "install unknown apps" for your file manager). It is an unsigned **debug** build for testing, not a Play Store release.

The workflow uses JDK 17, `platform-tools`, `platforms;android-35`, `build-tools;35.0.0` (the obsolete `tools` package is not used), Gradle 8.9 and generates the Gradle Wrapper during the run.

## Turn on real AI

The app never contains an AI key. It talks to your backend (`POST /v1/chat`); the backend holds the key.

1. Read `backend/README.md` and deploy the backend (Render/Railway/Fly.io, any host that provides HTTPS).
2. Set environment variables there (`AI_PROVIDER`, `ANTHROPIC_API_KEY` …). See `backend/.env.example`.
3. Tell the app the URL — either:
   - in the app: **Settings → AI & chat → Backend URL**, or
   - at build time: in GitHub go to *Settings → Secrets and variables → Actions → Variables* and add `NUSUK_BACKEND_URL` (a URL is not a secret).

Without a backend URL the app runs in **offline demo mode**: it shows matching text from the stored learning content (clearly labelled as demo, not AI).

## Features

Home · AI assistant (history, copy, share, loading, error + retry, follow-ups via conversation history) · Hajj and Umrah learning (steps, Arabic terms, references, "mark completed") · Quiz (one question at a time, submit → feedback + explanation → next, progress, final score, wrong-answer review, restart, history) · Profile (name, photo, language, progress, history) · Settings (language, light/dark/system, font size, daily reminder, floating button, chat history on/off, backend URL, privacy, about, delete all data) · onboarding + splash + adaptive icon.

### Floating AI button (how it stays within Android rules)
Off by default. Turning it on shows an explanation, then sends you to Android's own "Display over other apps" screen. It runs as a visible foreground service with a notification that has a **Turn off** button. If the permission is revoked, the app switches it off. Needs Android 6+ (uses `TYPE_APPLICATION_OVERLAY` on 8+).

## Project layout

```
settings.gradle.kts, build.gradle.kts, gradle.properties, gradle/wrapper/
.github/workflows/build-apk.yml      CI: backend tests + debug APK
app/build.gradle.kts
app/src/main/AndroidManifest.xml
app/src/main/assets/                 learning_content.json, questions.json (offline)
app/src/main/java/com/nusukai/app/
  NusukApp, MainActivity, OnboardingActivity, BaseActivity
  data/   models, ContentRepository, ChatApi, ChatRepository, DemoAnswerEngine, QuizRepository, db/NusukDb
  ui/     home, learn, chat, quiz, profile, settings   (Fragment + ViewModel, ViewBinding)
  overlay/FloatingButtonService
  util/   SettingsStore, LocaleHelper, Reminder*, Notifications
app/src/main/res/                    values (bn default), values-en, values-ar, layouts, icons
backend/                             zero-dependency Node.js API (see backend/README.md)
docs/                                ARCHITECTURE.md, CONTENT_GUIDE.md
```

## Adding more verified questions
Edit `app/src/main/assets/questions.json` (format in `docs/CONTENT_GUIDE.md`). Each question needs text, 4 options and an explanation in bn/en/ar plus a reference. Only add rulings you have checked with a qualified scholar. When the bank reaches 100/200/300 questions the matching session sizes switch on automatically (questions are never repeated within a session).

## Security checklist
- No API keys in the APK, Gradle files or repository (`.env` is git-ignored).
- HTTPS only (`usesCleartextTraffic=false`, app rejects non-https backend URLs).
- Backend: input validation, size limit, rate limiting, timeouts, no logging of questions, secrets from environment variables.
- Local data (profile, chats, quiz history) is in app-private storage and excluded from backup (`allowBackup=false`); Settings can delete it all.
- Permissions: `INTERNET`; optional `SYSTEM_ALERT_WINDOW`, `FOREGROUND_SERVICE(_SPECIAL_USE)`, `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED` (reminder reschedule).
- Known limit: anyone can call your backend URL, so keep the rate limit on and set a spending cap at your AI provider.

## বাংলায় সংক্ষেপে (সহজ নির্দেশ)
1. GitHub-এ একটি নতুন repository বানান এবং এই ফোল্ডারের সব ফাইল আপলোড করুন (`.github` ফোল্ডারসহ)।
2. **Actions** ট্যাবে গিয়ে বিল্ড শেষ হওয়ার অপেক্ষা করুন। সবুজ টিক এলে নিচে **Artifacts** থেকে `Nusuk-AI-debug-apk` নামান।
3. জিপ খুলে `app-debug.apk` ফোনে ইনস্টল করুন।
4. বিল্ড লাল হলে ব্যর্থ ধাপের লগ কপি করে আমাকে দিন; আমি ঠিক করে দেব। (এই প্রজেক্ট এখনো কম্পাইল করে পরীক্ষা করা হয়নি।)
5. আসল AI চাইলে `backend/README.md` দেখে backend চালু করুন, তারপর অ্যাপের **সেটিংস → AI ও চ্যাট → Backend URL**-এ HTTPS ঠিকানা দিন। API key কখনো অ্যাপে বা GitHub কোডে রাখবেন না।
6. প্রশ্ন ও শেখার বিষয়বস্তু ছাপার আগে একজন যোগ্য আলেম দিয়ে যাচাই করিয়ে নিন।
