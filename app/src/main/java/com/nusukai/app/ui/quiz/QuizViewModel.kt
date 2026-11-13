package com.nusukai.app.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nusukai.app.data.QuizItem
import com.nusukai.app.data.QuizResult
import com.nusukai.app.util.nusuk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class QuizPhase { SETUP, RUNNING, FINISHED }

data class QuizUi(
    val phase: QuizPhase = QuizPhase.SETUP,
    val items: List<QuizItem> = emptyList(),
    val index: Int = 0,
    val selected: Int = -1,          // display position the user tapped (-1 = none)
    val submitted: Boolean = false,  // true after "Submit answer"
    val answers: List<Int> = emptyList(), // submitted display position per question (-1 = none)
    val size: Int = 10
) {
    val current: QuizItem? get() = items.getOrNull(index)
    val correctCount: Int get() = answers.indices.count { answers[it] == items[it].correctPos }
}

/** Activity-scoped, so a running quiz survives switching tabs. */
class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = application.nusuk.quizRepository

    private val _ui = MutableStateFlow(QuizUi())
    val ui: StateFlow<QuizUi> = _ui.asStateFlow()

    private val _history = MutableStateFlow<List<QuizResult>>(emptyList())
    val history: StateFlow<List<QuizResult>> = _history.asStateFlow()

    val availableCount: Int get() = repo.availableCount

    init {
        refreshHistory()
    }

    fun refreshHistory() {
        viewModelScope.launch { _history.value = repo.history(20) }
    }

    fun start(requestedSize: Int) {
        val items = repo.buildSession(requestedSize)
        if (items.isEmpty()) return
        _ui.value = QuizUi(
            phase = QuizPhase.RUNNING, items = items, index = 0,
            answers = List(items.size) { -1 }, size = requestedSize
        )
    }

    fun select(position: Int) {
        _ui.update { if (it.phase == QuizPhase.RUNNING && !it.submitted) it.copy(selected = position) else it }
    }

    /** Returns false if no answer is selected yet. */
    fun submit(): Boolean {
        val s = _ui.value
        if (s.phase != QuizPhase.RUNNING || s.submitted) return true
        if (s.selected == -1) return false
        val newAnswers = s.answers.toMutableList().also { it[s.index] = s.selected }
        _ui.value = s.copy(submitted = true, answers = newAnswers)
        return true
    }

    /** Moves on only after the current answer was submitted. */
    fun next() {
        val s = _ui.value
        if (s.phase != QuizPhase.RUNNING || !s.submitted) return
        if (s.index + 1 < s.items.size) {
            _ui.value = s.copy(index = s.index + 1, selected = -1, submitted = false)
        } else {
            _ui.value = s.copy(phase = QuizPhase.FINISHED)
            viewModelScope.launch {
                repo.saveResult(s.items.size, s.correctCount)
                _history.value = repo.history(20)
            }
        }
    }

    fun restart() = start(_ui.value.size)

    fun backToSetup() {
        _ui.value = QuizUi(size = _ui.value.size)
        refreshHistory()
    }
}
