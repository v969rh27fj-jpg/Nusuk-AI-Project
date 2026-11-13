package com.nusukai.app.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import com.nusukai.app.BuildConfig
import com.nusukai.app.R
import com.nusukai.app.databinding.FragmentSettingsBinding
import com.nusukai.app.overlay.FloatingButtonService
import com.nusukai.app.util.LocaleHelper
import com.nusukai.app.util.ReminderScheduler
import com.nusukai.app.util.SettingsStore
import com.nusukai.app.util.UiHelpers
import com.nusukai.app.util.nusuk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settings: SettingsStore

    /** True while we change a switch from code, so its listener is ignored. */
    private var updatingUi = false
    private var pendingReminderEnable = false

    private val overlayLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { onOverlayResult() }

    private val notificationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (pendingReminderEnable) {
                pendingReminderEnable = false
                if (granted) enableReminder() else {
                    setSwitch(binding.switchReminder, false)
                    Toast.makeText(requireContext(), R.string.notif_permission_needed, Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settings = requireContext().nusuk.settings
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        UiHelpers.bindLanguageToggle(
            binding.toggleLang, binding.btnLangBn, binding.btnLangEn, binding.btnLangAr, settings
        )
        setupTheme()
        setupFont()
        setupReminder()
        setupFloating()
        setupChat()
        setupPrivacy()
    }

    // ---------------- appearance ----------------

    private fun setupTheme() {
        binding.toggleTheme.check(
            when (settings.themeMode) {
                1 -> R.id.btnThemeLight
                2 -> R.id.btnThemeDark
                else -> R.id.btnThemeSystem
            }
        )
        binding.toggleTheme.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val mode = when (checkedId) {
                R.id.btnThemeLight -> 1
                R.id.btnThemeDark -> 2
                else -> 0
            }
            if (mode != settings.themeMode) {
                settings.themeMode = mode
                LocaleHelper.applyTheme(mode) // recreates the screen
            }
        }
    }

    private fun setupFont() {
        val labels = intArrayOf(R.string.font_small, R.string.font_normal, R.string.font_large, R.string.font_xlarge)
        binding.sliderFont.value = settings.fontIndex.toFloat()
        binding.tvFontValue.setText(labels[settings.fontIndex])
        var tracking = false
        fun applyFont(index: Int) {
            if (index == settings.fontIndex) return
            settings.fontIndex = index
            requireActivity().recreate() // re-applies the font scale
        }
        binding.sliderFont.addOnChangeListener { _, value, fromUser ->
            if (!fromUser) return@addOnChangeListener
            binding.tvFontValue.setText(labels[value.toInt()])
            if (!tracking) applyFont(value.toInt()) // e.g. TalkBack / keyboard adjustments
        }
        binding.sliderFont.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) { tracking = true }
            override fun onStopTrackingTouch(slider: Slider) {
                tracking = false
                applyFont(slider.value.toInt())
            }
        })
    }

    // ---------------- daily reminder ----------------

    private fun setupReminder() {
        setSwitch(binding.switchReminder, settings.dailyReminder)
        binding.switchReminder.setOnCheckedChangeListener { _, checked ->
            if (updatingUi) return@setOnCheckedChangeListener
            if (!checked) {
                settings.dailyReminder = false
                ReminderScheduler.cancel(requireContext())
            } else if (Build.VERSION.SDK_INT >= 33 && !hasNotificationPermission()) {
                pendingReminderEnable = true
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                enableReminder()
            }
        }
    }

    private fun enableReminder() {
        settings.dailyReminder = true
        ReminderScheduler.schedule(requireContext())
    }

    private fun hasNotificationPermission() =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    // ---------------- floating button ----------------

    private fun setupFloating() {
        setSwitch(
            binding.switchFloating,
            settings.floatingEnabled && Settings.canDrawOverlays(requireContext())
        )
        binding.switchFloating.setOnCheckedChangeListener { _, checked ->
            if (updatingUi) return@setOnCheckedChangeListener
            if (!checked) {
                settings.floatingEnabled = false
                FloatingButtonService.stop(requireContext())
            } else if (Settings.canDrawOverlays(requireContext())) {
                enableFloating()
            } else {
                // Explain first. Never request the permission silently.
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.float_explain_title)
                    .setMessage(R.string.float_explain_body)
                    .setPositiveButton(R.string.float_grant) { _, _ ->
                        overlayLauncher.launch(
                            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireContext().packageName))
                        )
                    }
                    .setNegativeButton(R.string.cancel) { _, _ -> setSwitch(binding.switchFloating, false) }
                    .setOnCancelListener { setSwitch(binding.switchFloating, false) }
                    .show()
            }
        }
    }

    private fun onOverlayResult() {
        if (_binding == null) return
        if (Settings.canDrawOverlays(requireContext())) {
            setSwitch(binding.switchFloating, true)
            enableFloating()
        } else {
            setSwitch(binding.switchFloating, false)
            Toast.makeText(requireContext(), R.string.float_permission_denied, Toast.LENGTH_LONG).show()
        }
    }

    private fun enableFloating() {
        settings.floatingEnabled = true
        FloatingButtonService.start(requireContext())
    }

    private fun setSwitch(view: com.google.android.material.materialswitch.MaterialSwitch, checked: Boolean) {
        updatingUi = true
        view.isChecked = checked
        updatingUi = false
    }

    // ---------------- AI & chat ----------------

    private fun setupChat() {
        setSwitch(binding.switchSaveChat, settings.saveChatHistory)
        binding.switchSaveChat.setOnCheckedChangeListener { _, checked ->
            if (!updatingUi) settings.saveChatHistory = checked
        }

        binding.etBackend.setText(settings.backendUrl)
        binding.btnSaveBackend.setOnClickListener { saveBackend() }
        binding.etBackend.setOnEditorActionListener { _, _, _ -> saveBackend(); true }
    }

    private fun saveBackend() {
        val value = binding.etBackend.text?.toString().orEmpty().trim()
        if (value.isNotEmpty() && (!value.startsWith("https://", ignoreCase = true) || value.length > 200 || value.contains(" "))) {
            binding.tilBackend.error = getString(R.string.settings_backend_invalid)
            return
        }
        binding.tilBackend.error = null
        settings.backendUrl = value
        Toast.makeText(requireContext(), R.string.profile_saved, Toast.LENGTH_SHORT).show()
    }

    // ---------------- privacy ----------------

    private fun setupPrivacy() {
        binding.btnPrivacy.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.settings_privacy)
                .setMessage(R.string.privacy_text)
                .setPositiveButton(R.string.ok, null)
                .show()
        }
        binding.btnAbout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.settings_about)
                .setMessage(getString(R.string.about_text, BuildConfig.VERSION_NAME))
                .setPositiveButton(R.string.ok, null)
                .show()
        }
        binding.btnClearData.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.settings_clear_confirm)
                .setPositiveButton(R.string.delete) { _, _ -> clearAllData() }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun clearAllData() {
        val app = requireContext().nusuk
        val appContext = requireContext().applicationContext
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                app.db.clearAll()
                File(appContext.filesDir, "avatar.jpg").delete()
            }
            app.settings.userName = ""
            app.settings.avatarPath = ""
            Toast.makeText(appContext, R.string.settings_cleared, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
