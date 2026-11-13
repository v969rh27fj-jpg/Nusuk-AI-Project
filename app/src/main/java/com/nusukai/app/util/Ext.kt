package com.nusukai.app.util

import android.content.Context
import com.nusukai.app.NusukApp

/** Quick access to the application object (settings, database, repositories). */
val Context.nusuk: NusukApp get() = applicationContext as NusukApp

/** Picks the text for [lang], falling back to Bengali, then to anything available. */
fun <T> Map<String, T>.pick(lang: String): T = this[lang] ?: this["bn"] ?: this.values.first()
