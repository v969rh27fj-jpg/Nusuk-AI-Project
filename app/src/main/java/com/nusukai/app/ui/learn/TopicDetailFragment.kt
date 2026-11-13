package com.nusukai.app.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.nusukai.app.R
import com.nusukai.app.data.Topic
import com.nusukai.app.databinding.FragmentTopicDetailBinding
import com.nusukai.app.databinding.ItemStepBinding
import com.nusukai.app.util.nusuk
import com.nusukai.app.util.pick
import kotlinx.coroutines.launch

class TopicDetailFragment : Fragment() {

    private var _binding: FragmentTopicDetailBinding? = null
    private val binding get() = _binding!!
    private var isDone = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTopicDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val app = requireContext().nusuk
        val topic: Topic? = arguments?.getString(ARG_ID)?.let { app.content.topicById(it) }
        if (topic == null) {
            parentFragmentManager.popBackStack()
            return
        }
        val lang = app.settings.language

        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.tvTitle.text = topic.title.pick(lang)
        binding.tvArabic.text = topic.arabic
        binding.tvSummary.text = topic.summary.pick(lang)
        binding.tvRefs.text = topic.references.joinToString("\n") { "• $it" }

        topic.steps.pick(lang).forEachIndexed { i, text ->
            val row = ItemStepBinding.inflate(layoutInflater, binding.stepsContainer, false)
            row.tvStepNum.text = (i + 1).toString()
            row.tvStepText.text = text
            binding.stepsContainer.addView(row.root)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            isDone = topic.id in app.quizRepository.doneTopics()
            if (_binding != null) renderDone()
        }
        binding.btnDone.setOnClickListener {
            isDone = !isDone
            renderDone()
            viewLifecycleOwner.lifecycleScope.launch { app.quizRepository.setTopicDone(topic.id, isDone) }
        }
        renderDone()
    }

    private fun renderDone() {
        binding.btnDone.setText(if (isDone) R.string.learn_done else R.string.learn_mark_done)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_ID = "topic_id"
        fun newInstance(topicId: String) = TopicDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_ID, topicId) }
        }
    }
}
