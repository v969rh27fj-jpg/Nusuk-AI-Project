package com.nusukai.app

import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.nusukai.app.databinding.ActivityOnboardingBinding
import com.nusukai.app.util.UiHelpers
import com.nusukai.app.util.nusuk

/** Three short welcome pages. The user can also pick the language here. */
class OnboardingActivity : BaseActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private var page = 0

    private val titles = intArrayOf(R.string.onb1_title, R.string.onb2_title, R.string.onb3_title)
    private val bodies = intArrayOf(R.string.onb1_body, R.string.onb2_body, R.string.onb3_body)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        page = savedInstanceState?.getInt(KEY_PAGE, 0) ?: 0

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = bars.left + dp(24), top = bars.top + dp(24),
                right = bars.right + dp(24), bottom = bars.bottom + dp(24)
            )
            insets
        }

        UiHelpers.bindLanguageToggle(
            binding.toggleLang, binding.btnLangBn, binding.btnLangEn, binding.btnLangAr, nusuk.settings
        )
        binding.btnNext.setOnClickListener {
            if (page < titles.size - 1) {
                page++
                render()
            } else {
                finishOnboarding()
            }
        }
        binding.btnSkip.setOnClickListener { finishOnboarding() }
        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_PAGE, page)
    }

    private fun render() {
        binding.tvTitle.setText(titles[page])
        binding.tvBody.setText(bodies[page])
        binding.tvDots.text = titles.indices.joinToString("  ") { if (it == page) "●" else "○" }
        binding.btnNext.setText(if (page == titles.size - 1) R.string.onb_start else R.string.onb_next)
    }

    private fun finishOnboarding() {
        nusuk.settings.onboardingDone = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val KEY_PAGE = "page"
    }
}
