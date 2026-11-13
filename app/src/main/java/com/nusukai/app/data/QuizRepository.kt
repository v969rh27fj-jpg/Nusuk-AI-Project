package com.nusukai.app.data

import com.nusukai.app.data.db.NusukDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository(private val content: ContentRepository, private val db: NusukDb) {

    val availableCount: Int get() = content.questions.size

    /** Picks up to [size] DIFFERENT questions at random (never repeats a question) and shuffles each one's options. */
    fun buildSession(size: Int): List<QuizItem> {
        return content.questions.shuffled().take(size).map { q ->
            val order = (0 until q.optionCount).shuffled()
            QuizItem(q, order, order.indexOf(q.answer))
        }
    }

    suspend fun saveResult(total: Int, correct: Int) = withContext(Dispatchers.IO) {
        db.insertQuizResult(total, correct, System.currentTimeMillis())
    }

    suspend fun history(limit: Int): List<QuizResult> = withContext(Dispatchers.IO) { db.getQuizResults(limit) }

    suspend fun doneTopics(): Set<String> = withContext(Dispatchers.IO) { db.doneTopicIds() }

    suspend fun setTopicDone(topicId: String, done: Boolean) = withContext(Dispatchers.IO) {
        db.setTopicDone(topicId, done)
    }
}
