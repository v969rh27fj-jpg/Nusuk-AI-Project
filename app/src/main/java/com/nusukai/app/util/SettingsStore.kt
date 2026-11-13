package com.nusukai.app.util

import android.content.Context
import com.nusukai.app.BuildConfig

/** All user preferences, stored only on this device (SharedPreferences). */
class SettingsStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("nusuk_settings", Context.MODE_PRIVATE)

    var language: String
        get() = prefs.getString(K_LANGUAGE, "bn") ?: "bn"
        set(value) = prefs.edit().putString(K_LANGUAGE, value).apply()

    /** 0 = follow system, 1 = light, 2 = dark */
    var themeMode: Int
        get() = prefs.getInt(K_THEME, 0)
        set(value) = prefs.edit().putInt(K_THEME, value).apply()

    /** Index into [FONT_MULTIPLIERS]. 1 = normal. */
    var fontIndex: Int
        get() = prefs.getInt(K_FONT, 1).coerceIn(0, FONT_MULTIPLIERS.size - 1)
        set(value) = prefs.edit().putInt(K_FONT, value).apply()

    val fontMultiplier: Float get() = FONT_MULTIPLIERS[fontIndex]

    var dailyReminder: Boolean
        get() = prefs.getBoolean(K_REMINDER, false)
        set(value) = prefs.edit().putBoolean(K_REMINDER, value).apply()

    var floatingEnabled: Boolean
        get() = prefs.getBoolean(K_FLOATING, false)
        set(value) = prefs.edit().putBoolean(K_FLOATING, value).apply()

    var saveChatHistory: Boolean
        get() = prefs.getBoolean(K_SAVE_CHAT, true)
        set(value) = prefs.edit().putBoolean(K_SAVE_CHAT, value).apply()

    var userName: String
        get() = prefs.getString(K_NAME, "") ?: ""
        set(value) = prefs.edit().putString(K_NAME, value).apply()

    var avatarPath: String
        get() = prefs.getString(K_AVATAR, "") ?: ""
        set(value) = prefs.edit().putString(K_AVATAR, value).apply()

    var onboardingDone: Boolean
        get() = prefs.getBoolean(K_ONBOARDING, false)
        set(value) = prefs.edit().putBoolean(K_ONBOARDING, value).apply()

    /** Backend URL typed by the user in Settings. Empty = use the build-time default. */
    var backendUrl: String
        get() = prefs.getString(K_BACKEND, "") ?: ""
        set(value) = prefs.edit().putString(K_BACKEND, value).apply()

    /** The backend that is actually used. Empty string = offline demo mode. */
    fun effectiveBackendUrl(): String {
        val custom = backendUrl.trim()
        return if (custom.isNotEmpty()) custom else BuildConfig.BACKEND_URL.trim()
    }

    companion object {
        val FONT_MULTIPLIERS = floatArrayOf(0.85f, 1.0f, 1.15f, 1.3f)
        private const val K_LANGUAGE = "language"
        private const val K_THEME = "theme"
        private const val K_FONT = "font_index"
        private const val K_REMINDER = "daily_reminder"
        private const val K_FLOATING = "floating_enabled"
        private const val K_SAVE_CHAT = "save_chat"
        private const val K_NAME = "user_name"
        private const val K_AVATAR = "avatar_path"
        private const val K_ONBOARDING = "onboarding_done"
        private const val K_BACKEND = "backend_url"
    }
}
