package com.nusukai.app.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nusukai.app.data.ChatError
import com.nusukai.app.data.ChatMessage
import com.nusukai.app.data.ChatResult
import com.nusukai.app.util.nusuk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val loading: Boolean = false,
    val error: ChatError? = null,
    val demo: Boolean = false
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application.nusuk
    private val repo = app.chatRepository
    private val settings = app.settings

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    private var nextId = 1L

    init {
        if (settings.saveChatHistory) {
            viewModelScope.launch {
                val saved = repo.loadHistory()
                if (saved.isNotEmpty()) {
                    nextId = saved.maxOf { it.id } + 1
                    // Do not overwrite messages typed while the history was loading.
                    _state.update { it.copy(messages = saved + it.messages) }
                }
            }
        }
    }

    fun send(text: String) {
        val clean = text.trim()
        if (clean.isEmpty() || _state.value.loading) return
        val message = ChatMessage(nextId++, ChatMessage.ROLE_USER, clean, System.currentTimeMillis())
        _state.update { it.copy(messages = it.messages + message, error = null) }
        if (settings.saveChatHistory) viewModelScope.launch { repo.save(message.role, message.content) }
        request()
    }

    /** Re-sends the last question after an error. */
    fun retry() {
        if (_state.value.loading) return
        if (_state.value.messages.lastOrNull()?.isUser == true) {
            _state.update { it.copy(error = null) }
            request()
        }
    }

    fun clear() {
        _state.update { ChatUiState() }
        viewModelScope.launch { repo.clearHistory() }
    }

    private fun request() {
        _state.update { it.copy(loading = true, error = null) }
        val history = _state.value.messages
        viewModelScope.launch {
            when (val result = repo.send(history, settings.language)) {
                is ChatResult.Success -> {
                    val reply = ChatMessage(nextId++, ChatMessage.ROLE_ASSISTANT, result.reply, System.currentTimeMillis())
                    _state.update { it.copy(messages = it.messages + reply, loading = false, demo = result.demo) }
                    if (settings.saveChatHistory) repo.save(reply.role, reply.content)
                }
                is ChatResult.Failure -> {
                    _state.update { it.copy(loading = false, error = result.error) }
                }
            }
        }
    }
}
