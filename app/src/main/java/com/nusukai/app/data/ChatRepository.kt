package com.nusukai.app.data

import android.content.Context
import com.nusukai.app.data.db.NusukDb
import com.nusukai.app.util.SettingsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The single place the UI asks for AI answers.
 * Provider changes happen on the backend, so this class never needs to change when you switch AI providers.
 */
class ChatRepository(
    context: Context,
    private val settings: SettingsStore,
    content: ContentRepository,
    private val db: NusukDb
) {
    private val appContext = context.applicationContext
    private val demo = DemoAnswerEngine(content)

    suspend fun send(history: List<ChatMessage>, language: String): ChatResult = withContext(Dispatchers.IO) {
        val url = settings.effectiveBackendUrl()
        if (url.isBlank()) {
            val lastQuestion = history.lastOrNull { it.isUser }?.content ?: ""
            return@withContext ChatResult.Success(demo.answer(appContext, lastQuestion, language), demo = true)
        }
        try {
            val reply = ChatApi.chat(url, history, language)
            ChatResult.Success(reply.reply, reply.demo)
        } catch (e: ChatApi.ApiException) {
            ChatResult.Failure(e.error)
        }
    }

    suspend fun loadHistory(): List<ChatMessage> = withContext(Dispatchers.IO) { db.getChats() }

    suspend fun save(role: String, content: String) = withContext(Dispatchers.IO) {
        db.insertChat(role, content, System.currentTimeMillis())
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) { db.clearChats() }
}
