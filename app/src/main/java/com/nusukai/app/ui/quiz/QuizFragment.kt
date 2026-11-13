package com.nusukai.app.ui.quiz

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.nusukai.app.R
import com.nusukai.app.data.QuizResult
import com.nusukai.app.databinding.FragmentQuizBinding
import com.nusukai.app.databinding.ItemOptionBinding
import com.nusukai.app.databinding.ItemReviewBinding
import com.nusukai.app.util.SettingsStore
import com.nusukai.app.util.nusuk
import com.nusukai.app.util.pick
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

/** One fragment, three states: setup -> running -> result. */
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val vm: QuizViewModel by activityViewModels()
    private lateinit var settings: SettingsStore
    private var selectedSize = 10

    private val sizes = listOf(10, 20, 100, 200, 300)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settings = requireContext().nusuk.settings
        setupChips()

        binding.btnStart.setOnClickListener { vm.start(selectedSize) }
        binding.btnSubmit.setOnClickListener {
            if (!vm.submit()) Snackbar.make(binding.root, R.string.quiz_select_first, Snackbar.LENGTH_SHORT).show()
        }
        binding.btnNext.setOnClickListener { vm.next() }
        binding.btnRestart.setOnClickListener { vm.restart() }
        binding.btnBackToSetup.setOnClickListener { vm.backToSetup() }
        binding.btnExit.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.quiz_exit_confirm)
                .setPositiveButton(R.string.quiz_exit) { _, _ -> vm.backToSetup() }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.ui.combine(vm.history) { ui, history -> ui to history }
                    .collect { (ui, history) -> render(ui, history) }
            }
        }
    }

    private fun setupChips() {
        val available = vm.availableCount
        binding.chipGroupCount.removeAllViews()
        var firstEnabledId = View.NO_ID
        for (size in sizes) {
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = getString(R.string.quiz_count_n, size)
                isCheckable = true
                isEnabled = size <= available
                setOnClickListener { selectedSize = size }
            }
            binding.chipGroupCount.addView(chip)
            if (chip.isEnabled && firstEnabledId == View.NO_ID) {
                firstEnabledId = chip.id
                selectedSize = size
            }
        }
        if (firstEnabledId != View.NO_ID) binding.chipGroupCount.check(firstEnabledId)

        val anyDisabled = sizes.any { it > available }
        binding.tvUnavailable.visibility = if (anyDisabled) View.VISIBLE else View.GONE
        binding.tvUnavailable.text = getString(R.string.quiz_unavailable, available)
        binding.btnStart.isEnabled = firstEnabledId != View.NO_ID
    }

    private fun render(ui: QuizUi, history: List<QuizResult>) {
        binding.scrollSetup.visibility = if (ui.phase == QuizPhase.SETUP) View.VISIBLE else View.GONE
        binding.layoutRun.visibility = if (ui.phase == QuizPhase.RUNNING) View.VISIBLE else View.GONE
        binding.scrollResult.visibility = if (ui.phase == QuizPhase.FINISHED) View.VISIBLE else View.GONE
        when (ui.phase) {
            QuizPhase.SETUP -> renderHistory(history)
            QuizPhase.RUNNING -> renderQuestion(ui)
            QuizPhase.FINISHED -> renderResult(ui)
        }
    }

    private fun renderHistory(history: List<QuizResult>) {
        binding.layoutHistory.removeAllViews()
        binding.tvHistoryEmpty.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        val df = DateFormat.getDateInstance(DateFormat.MEDIUM, com.nusukai.app.util.LocaleHelper.locale(settings.language))
        history.take(10).forEach { r ->
            val tv = TextView(requireContext()).apply {
                text = getString(R.string.quiz_history_row, df.format(Date(r.takenAt)), r.correct, r.total, r.percent)
                setTextColor(ContextCompat.getColor(context, R.color.nusuk_text))
                setPadding(0, 8, 0, 8)
            }
            binding.layoutHistory.addView(tv)
        }
    }

    private fun renderQuestion(ui: QuizUi) {
        val item = ui.current ?: return
        val lang = settings.language
        val q = item.question

        binding.tvProgress.text = getString(R.string.quiz_progress, ui.index + 1, ui.items.size)
        binding.quizProgress.max = ui.items.size
        binding.quizProgress.progress = ui.index + if (ui.submitted) 1 else 0
        binding.tvQuestion.text = q.text.pick(lang)

        val texts = q.options.pick(lang)
        binding.optionsContainer.removeAllViews()
        item.order.forEachIndexed { pos, originalIndex ->
            val row = ItemOptionBinding.inflate(layoutInflater, binding.optionsContainer, false)
            row.tvOption.text = texts[originalIndex]
            styleOption(row, pos, ui, item.correctPos)
            row.cardOption.setOnClickListener { vm.select(pos) }
            row.cardOption.isEnabled = !ui.submitted
            binding.optionsContainer.addView(row.root)
        }

        binding.layoutFeedback.visibility = if (ui.submitted) View.VISIBLE else View.GONE
        if (ui.submitted) {
            val correct = ui.answers[ui.index] == item.correctPos
            binding.tvFeedback.setText(if (correct) R.string.quiz_correct else R.string.quiz_incorrect)
            binding.tvFeedback.setTextColor(
                ContextCompat.getColor(requireContext(), if (correct) R.color.nusuk_success else R.color.nusuk_error)
            )
            binding.tvCorrectAnswer.text = getString(R.string.quiz_correct_answer_is, texts[q.answer])
            binding.tvExplanation.text = getString(R.string.quiz_explanation) + ": " + q.explanation.pick(lang)
            binding.tvReference.visibility = if (q.reference.isBlank()) View.GONE else View.VISIBLE
            binding.tvReference.text = getString(R.string.quiz_reference, q.reference)
        }

        // "Next" is only offered after the answer was submitted.
        binding.btnSubmit.visibility = if (ui.submitted) View.GONE else View.VISIBLE
        binding.btnNext.visibility = if (ui.submitted) View.VISIBLE else View.GONE
        binding.btnNext.setText(if (ui.index + 1 == ui.items.size) R.string.quiz_finish else R.string.quiz_next)
    }

    private fun styleOption(row: ItemOptionBinding, pos: Int, ui: QuizUi, correctPos: Int) {
        val ctx = requireContext()
        val color = when {
            ui.submitted && pos == correctPos -> ContextCompat.getColor(ctx, R.color.nusuk_success)
            ui.submitted && pos == ui.answers[ui.index] -> ContextCompat.getColor(ctx, R.color.nusuk_error)
            !ui.submitted && pos == ui.selected -> ContextCompat.getColor(ctx, R.color.nusuk_secondary)
            else -> ContextCompat.getColor(ctx, R.color.nusuk_outline)
        }
        row.cardOption.setStrokeColor(ColorStateList.valueOf(color))
        row.cardOption.strokeWidth = if (color == ContextCompat.getColor(ctx, R.color.nusuk_outline)) 3 else 6
    }

    private fun renderResult(ui: QuizUi) {
        val lang = settings.language
        val total = ui.items.size
        val correct = ui.correctCount
        binding.tvScore.text = getString(R.string.quiz_score, correct, total)
        binding.tvPercent.text = "${if (total == 0) 0 else Math.round(correct * 100f / total)}%"
        binding.tvCorrectCount.text = getString(R.string.quiz_correct_count, correct)
        binding.tvWrongCount.text = getString(R.string.quiz_wrong_count, total - correct)

        binding.layoutReview.removeAllViews()
        val wrong = ui.items.indices.filter { ui.answers[it] != ui.items[it].correctPos }
        binding.tvAllCorrect.visibility = if (wrong.isEmpty()) View.VISIBLE else View.GONE
        binding.tvReviewTitle.visibility = if (wrong.isEmpty()) View.GONE else View.VISIBLE
        for (i in wrong) {
            val item = ui.items[i]
            val q = item.question
            val texts = q.options.pick(lang)
            val row = ItemReviewBinding.inflate(layoutInflater, binding.layoutReview, false)
            row.tvReviewQuestion.text = q.text.pick(lang)
            val given = ui.answers[i]
            row.tvReviewYours.text = if (given >= 0) getString(R.string.quiz_your_answer, texts[item.order[given]]) else ""
            row.tvReviewYours.visibility = if (given >= 0) View.VISIBLE else View.GONE
            row.tvReviewCorrect.text = getString(R.string.quiz_correct_answer_is, texts[q.answer])
            row.tvReviewExplanation.text = q.explanation.pick(lang)
            binding.layoutReview.addView(row.root)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
