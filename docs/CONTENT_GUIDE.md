# Content guide

`assets/learning_content.json`: topics with `id, category(hajj|umrah), order, arabic, title{bn,en,ar}, summary{bn,en,ar}, steps{bn:[...],en:[...],ar:[...]}, references[]`.

`assets/questions.json` question format:
```json
{ "id": "q23", "topic": "hajj_tawaf",
  "q": {"bn": "...", "en": "...", "ar": "..."},
  "options": {"bn": ["a","b","c","d"], "en": ["a","b","c","d"], "ar": ["a","b","c","d"]},
  "answer": 2,
  "explanation": {"bn": "...", "en": "...", "ar": "..."},
  "reference": "Qur'an 2:158" }
```
`answer` is the 0-based index of the correct option (same index in all three languages). The app shuffles options each time. Malformed questions are skipped, not crashed on.

**Review rule:** do not publish a ruling you have not checked against reliable sources and a qualified scholar. The shipped files are marked `"reviewed": false`.
