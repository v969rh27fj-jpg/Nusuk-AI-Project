package com.nusukai.app.util

import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

object UiHelpers {

    /** Wires a 3-button language toggle (Bengali / English / Arabic) to the settings. */
    fun bindLanguageToggle(
        group: MaterialButtonToggleGroup,
        bn: MaterialButton,
        en: MaterialButton,
        ar: MaterialButton,
        settings: SettingsStore
    ) {
        val current = when (settings.language) {
            "en" -> en.id
            "ar" -> ar.id
            else -> bn.id
        }
        group.check(current)
        group.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val lang = when (checkedId) {
                en.id -> "en"
                ar.id -> "ar"
                else -> "bn"
            }
            if (lang != settings.language) {
                settings.language = lang
                LocaleHelper.applyLanguage(lang) // recreates the visible activity
            }
        }
    }
}
