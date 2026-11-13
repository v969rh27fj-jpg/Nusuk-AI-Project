package com.nusukai.app.data

import android.content.Context
import com.nusukai.app.R
import com.nusukai.app.util.LocaleHelper
import com.nusukai.app.util.pick

/**
 * Offline "demo mode" used when no backend URL is configured.
 * It is NOT AI: it only finds the matching topic in the stored learning content and shows it.
 */
class DemoAnswerEngine(private val content: ContentRepository) {

    private class Key(val words: List<String>, val hajjId: String?, val umrahId: String?)

    private val keys = listOf(
        Key(listOf("ihram", "ইহরাম", "إحرام", "الإحرام"), "hajj_ihram", "umrah_ihram"),
        Key(listOf("miqat", "মীকাত", "মিকাত", "ميقات"), "hajj_miqat", "hajj_miqat"),
        Key(listOf("mina", "মিনা", "منى"), "hajj_mina", null),
        Key(listOf("arafat", "arafah", "আরাফ", "عرفة", "عرفات"), "hajj_arafat", null),
        Key(listOf("muzdalifah", "muzdalifa", "মুযদালিফা", "মুজদালিফা", "মুযদালিফ", "مزدلفة"), "hajj_muzdalifah", null),
        Key(listOf("jamarat", "jamrah", "stoning", "pebbles", "কঙ্কর", "জামারা", "জামরা", "جمرات", "الجمار", "رمي"), "hajj_jamarat", null),
        Key(listOf("tawaf", "তাওয়াফ", "طواف"), "hajj_tawaf", "umrah_tawaf"),
        Key(listOf("sa'i", "sai", "saee", "সাঈ", "সায়ী", "سعي"), "hajj_sai", "umrah_sai"),
        Key(listOf("halq", "taqsir", "shave", "trim", "হালক", "তাকসীর", "মুণ্ডন", "حلق", "تقصير"), "hajj_halq", "umrah_halq")
    )

    private val umrahWords = listOf("umrah", "umra", "উমরা", "উমরাহ", "عمرة")
    private val hajjWords = listOf("hajj", "হজ", "الحج")

    private fun matches(query: String, word: String): Boolean {
        val ascii = word.all { it in 'a'..'z' || it == '\'' }
        return if (ascii) {
            Regex("(?<![a-z])" + Regex.escape(word) + "(?![a-z])").containsMatchIn(query)
        } else {
            query.contains(word)
        }
    }

    private fun matchesAny(query: String, words: List<String>) = words.any { matches(query, it) }

    fun answer(context: Context, question: String, language: String): String {
        val loc = LocaleHelper.localizedContext(context, language)
        val prefix = loc.getString(R.string.chat_demo_prefix)
        val q = question.lowercase()
        val wantsUmrah = matchesAny(q, umrahWords)
        val wantsHajj = matchesAny(q, hajjWords)

        val key = keys.firstOrNull { matchesAny(q, it.words) }
        if (key != null) {
            val id = if (wantsHajj && !wantsUmrah) key.hajjId ?: key.umrahId
            else if (wantsUmrah) key.umrahId ?: key.hajjId
            else key.umrahId ?: key.hajjId
            val topic = id?.let { content.topicById(it) }
            if (topic != null) return "$prefix\n\n${formatTopic(topic, language)}"
        }

        val category = when {
            wantsUmrah && !wantsHajj -> "umrah"
            wantsHajj && !wantsUmrah -> "hajj"
            else -> null
        }
        if (category != null) {
            val list = content.topicsFor(category)
            if (list.isNotEmpty()) {
                val lines = list.mapIndexed { i, t -> "${i + 1}) ${t.title.pick(language)} — ${t.summary.pick(language)}" }
                return "$prefix\n\n" + lines.joinToString("\n\n")
            }
        }
        return loc.getString(R.string.chat_demo_nomatch)
    }

    private fun formatTopic(t: Topic, lang: String): String {
        val steps = t.steps.pick(lang).mapIndexed { i, s -> "${i + 1}) $s" }.joinToString("\n")
        return "${t.title.pick(lang)} — ${t.arabic}\n${t.summary.pick(lang)}\n\n$steps\n\n(${t.references.joinToString("; ")})"
    }
}
