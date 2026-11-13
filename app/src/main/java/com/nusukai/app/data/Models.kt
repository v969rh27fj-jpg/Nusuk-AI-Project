package com.nusukai.app.data

/** One Hajj/Umrah learning topic. Text fields are maps: language code -> text. */
data class Topic(
    val id: String,
    val category: String, // "hajj" or "umrah"
    val order: Int,
    val arabic: String,
    val title: Map<String, String>,
    val summary: Map<String, String>,
    val steps: Map<String, List<String>>,
    val references: List<String>
)

data class Question(
    val id: String,
    val topic: String,
    val text: Map<String, String>,
    val options: Map<String, List<String>>,
    val answer: Int,
    val explanation: Map<String, String>,
    val reference: String
) {
    val optionCount: Int get() = options.values.firstOrNull()?.size ?: 0
}

/** A question prepared for one quiz run: options are shuffled via [order]. */
data class QuizItem(
    val question: Question,
    val order: List<Int>,   // order[displayPosition] = original option index
    val correctPos: Int     // display position of the correct option
)

data class ChatMessage(
    val id: Long,
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long
) {
    val isUser: Boolean get() = role == ROLE_USER

    companion object {
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
    }
}

data class QuizResult(val id: Long, val takenAt: Long, val total: Int, val correct: Int) {
    val percent: Int get() = if (total == 0) 0 else Math.round(correct * 100f / total)
}

enum class ChatError { NETWORK, SERVER, RATE }

sealed class ChatResult {
    data class Success(val reply: String, val demo: Boolean) : ChatResult()
    data class Failure(val error: ChatError) : ChatResult()
}
