package com.nusukai.app

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.nusukai.app.util.SettingsStore

/** Applies the user's font-size choice to every screen. */
open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val config = Configuration(newBase.resources.configuration)
        config.fontScale = config.fontScale * SettingsStore(newBase).fontMultiplier
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }
}
