package com.nusukai.app.data

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

/** Loads the offline learning content and question bank from the app's assets (works with no internet). */
class ContentRepository(private val context: Context) {

    val topics: List<Topic> by lazy { loadTopics() }
    val questions: List<Question> by lazy { loadQuestions() }

    fun topicsFor(category: String): List<Topic> =
        topics.filter { it.category == category }.sortedBy { it.order }

    fun topicById(id: String): Topic? = topics.firstOrNull { it.id == id }

    fun dailyTopic(dayOfYear: Int): Topic? =
        if (topics.isEmpty()) null else topics[dayOfYear % topics.size]

    private fun readAsset(name: String): String =
        context.assets.open(name).bufferedReader(Charsets.UTF_8).use { it.readText() }

    private fun loadTopics(): List<Topic> = try {
        val arr = JSONObject(readAsset("learning_content.json")).getJSONArray("topics")
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            val stepsObj = o.getJSONObject("steps")
            Topic(
                id = o.getString("id"),
                category = o.getString("category"),
                order = o.getInt("order"),
                arabic = o.getString("arabic"),
                title = o.getJSONObject("title").toStringMap(),
                summary = o.getJSONObject("summary").toStringMap(),
                steps = stepsObj.keys().asSequence().associateWith { stepsObj.getJSONArray(it).toStringList() },
                references = o.getJSONArray("references").toStringList()
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "Could not load learning content", e)
        emptyList()
    }

    private fun loadQuestions(): List<Question> = try {
        val arr = JSONObject(readAsset("questions.json")).getJSONArray("questions")
        val result = ArrayList<Question>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val optObj = o.getJSONObject("options")
            val q = Question(
                id = o.getString("id"),
                topic = o.getString("topic"),
                text = o.getJSONObject("q").toStringMap(),
                options = optObj.keys().asSequence().associateWith { optObj.getJSONArray(it).toStringList() },
                answer = o.getInt("answer"),
                explanation = o.getJSONObject("explanation").toStringMap(),
                reference = o.optString("reference", "")
            )
            // Skip malformed entries instead of crashing.
            if (q.optionCount >= 2 && q.answer in 0 until q.optionCount) result.add(q)
        }
        result
    } catch (e: Exception) {
        Log.e(TAG, "Could not load question bank", e)
        emptyList()
    }

    private fun JSONObject.toStringMap(): Map<String, String> =
        keys().asSequence().associateWith { getString(it) }

    private fun JSONArray.toStringList(): List<String> = List(length()) { getString(it) }

    companion object {
        private const val TAG = "ContentRepository"
    }
}
