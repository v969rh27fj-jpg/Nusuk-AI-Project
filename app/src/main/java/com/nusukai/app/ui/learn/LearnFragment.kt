package com.nusukai.app.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.nusukai.app.MainActivity
import com.nusukai.app.databinding.FragmentLearnBinding
import com.nusukai.app.util.nusuk
import kotlinx.coroutines.launch

class LearnFragment : Fragment() {

    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TopicAdapter
    private var category = "hajj"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val main = requireActivity() as MainActivity
        category = savedInstanceState?.getString(KEY_CATEGORY) ?: main.pendingLearnCategory

        adapter = TopicAdapter { main.openTopic(it.id) }
        binding.rvTopics.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTopics.adapter = adapter

        binding.tabs.getTabAt(if (category == "umrah") 1 else 0)?.select()
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                category = if (tab.position == 1) "umrah" else "hajj"
                main.pendingLearnCategory = category
                refresh()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onResume() {
        super.onResume()
        refresh() // also refreshes the "completed" ticks after returning from a topic
    }

    private fun refresh() {
        val app = requireContext().nusuk
        val topics = app.content.topicsFor(category)
        viewLifecycleOwner.lifecycleScope.launch {
            val done = app.quizRepository.doneTopics()
            if (_binding == null) return@launch
            adapter.submit(topics, done, app.settings.language)
            binding.tvEmpty.visibility = if (topics.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CATEGORY, category)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val KEY_CATEGORY = "category"
    }
}
