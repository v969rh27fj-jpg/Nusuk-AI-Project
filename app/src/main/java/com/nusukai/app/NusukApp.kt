package com.nusukai.app

import android.app.Application
import com.nusukai.app.data.ChatRepository
import com.nusukai.app.data.ContentRepository
import com.nusukai.app.data.QuizRepository
import com.nusukai.app.data.db.NusukDb
import com.nusukai.app.util.LocaleHelper
import com.nusukai.app.util.SettingsStore

/** Holds the app-wide singletons (simple manual dependency injection, no extra library). */
class NusukApp : Application() {

    lateinit var settings: SettingsStore
        private set
    lateinit var db: NusukDb
        private set
    lateinit var content: ContentRepository
        private set
    lateinit var chatRepository: ChatRepository
        private set
    lateinit var quizRepository: QuizRepository
        private set

    override fun onCreate() {
        super.onCreate()
        settings = SettingsStore(this)
        db = NusukDb(this)
        content = ContentRepository(this)
        chatRepository = ChatRepository(this, settings, content, db)
        quizRepository = QuizRepository(content, db)

        LocaleHelper.applyTheme(settings.themeMode)
        LocaleHelper.applyLanguage(settings.language)
    }
}
