package com.nusukai.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nusukai.app.MainActivity
import com.nusukai.app.R
import com.nusukai.app.databinding.FragmentHomeBinding
import com.nusukai.app.util.nusuk
import com.nusukai.app.util.pick
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val app = requireContext().nusuk
        val main = requireActivity() as MainActivity
        val lang = app.settings.language

        val name = app.settings.userName.trim()
        binding.tvGreeting.text =
            if (name.isEmpty()) getString(R.string.greeting_plain) else getString(R.string.greeting_named, name)

        binding.btnSettings.setOnClickListener { main.openSettings() }
        binding.cardAi.setOnClickListener { main.selectTab(R.id.nav_ai) }
        binding.cardHajj.setOnClickListener { main.openLearn("hajj") }
        binding.cardUmrah.setOnClickListener { main.openLearn("umrah") }
        binding.cardQuiz.setOnClickListener { main.selectTab(R.id.nav_quiz) }

        val daily = app.content.dailyTopic(Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
        if (daily == null) {
            binding.cardDaily.visibility = View.GONE
        } else {
            binding.tvDailyTopic.text = daily.title.pick(lang)
            binding.tvDailySummary.text = daily.summary.pick(lang)
            binding.cardDaily.setOnClickListener { main.openTopic(daily.id) }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
