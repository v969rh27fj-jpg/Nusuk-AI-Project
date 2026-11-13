package com.nusukai.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.nusukai.app.databinding.ActivityMainBinding
import com.nusukai.app.overlay.FloatingButtonService
import com.nusukai.app.ui.chat.ChatFragment
import com.nusukai.app.ui.home.HomeFragment
import com.nusukai.app.ui.learn.LearnFragment
import com.nusukai.app.ui.learn.TopicDetailFragment
import com.nusukai.app.ui.profile.ProfileFragment
import com.nusukai.app.ui.quiz.QuizFragment
import com.nusukai.app.ui.settings.SettingsFragment
import com.nusukai.app.util.nusuk

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    /** Which tab (hajj/umrah) the Learn screen should open on. */
    var pendingLearnCategory: String = "hajj"

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (!nusuk.settings.onboardingDone) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Android 15 draws apps edge-to-edge: keep content clear of the status bar and keyboard.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.updatePadding(left = bars.left, top = bars.top, right = bars.right, bottom = ime.bottom)
            binding.bottomNav.isVisible = ime.bottom == 0
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment()).commit()
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            showRoot(
                when (item.itemId) {
                    R.id.nav_learn -> LearnFragment()
                    R.id.nav_ai -> ChatFragment()
                    R.id.nav_quiz -> QuizFragment()
                    R.id.nav_profile -> ProfileFragment()
                    else -> HomeFragment()
                }
            )
            true
        }
        binding.bottomNav.setOnItemReselectedListener { /* stay on the current screen */ }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (::binding.isInitialized) handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        if (!::binding.isInitialized) return
        // Keep the floating button consistent with the user's choice and Android's permission state.
        if (nusuk.settings.floatingEnabled) {
            if (Settings.canDrawOverlays(this)) {
                FloatingButtonService.start(this)
            } else {
                nusuk.settings.floatingEnabled = false // permission was revoked in system settings
                FloatingButtonService.stop(this)
            }
        }
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_OPEN_CHAT, false) == true) {
            intent.removeExtra(EXTRA_OPEN_CHAT)
            selectTab(R.id.nav_ai)
        }
    }

    // ---- navigation helpers used by fragments ----

    fun selectTab(itemId: Int) {
        binding.bottomNav.selectedItemId = itemId
    }

    fun openLearn(category: String) {
        pendingLearnCategory = category
        if (binding.bottomNav.selectedItemId == R.id.nav_learn) showRoot(LearnFragment())
        else selectTab(R.id.nav_learn)
    }

    fun openTopic(topicId: String) = push(TopicDetailFragment.newInstance(topicId))

    fun openSettings() = push(SettingsFragment())

    private fun showRoot(fragment: Fragment) {
        val fm = supportFragmentManager
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fm.beginTransaction().replace(R.id.container, fragment).commit()
    }

    private fun push(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val EXTRA_OPEN_CHAT = "open_chat"
    }
}
