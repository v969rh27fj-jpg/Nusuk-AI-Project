package com.nusukai.app.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.nusukai.app.data.ChatMessage
import com.nusukai.app.data.QuizResult

/**
 * Local SQLite database (private to the app, excluded from backup).
 *
 *  chat_messages  - chat history (only written if the user keeps "save chat history" on)
 *  quiz_results   - one row per finished quiz
 *  topic_progress - which learning topics the user marked as completed
 *
 * All methods are blocking: call them from a background dispatcher.
 */
class NusukDb(context: Context) : SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE chat_messages (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "role TEXT NOT NULL, content TEXT NOT NULL, created_at INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE quiz_results (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "taken_at INTEGER NOT NULL, total INTEGER NOT NULL, correct INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE topic_progress (topic_id TEXT PRIMARY KEY, " +
                "completed INTEGER NOT NULL, updated_at INTEGER NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Version 1 is the first release. Add migrations here when the schema changes.
    }

    // ---- chat ----
    fun insertChat(role: String, content: String, timestamp: Long) {
        val values = ContentValues().apply {
            put("role", role); put("content", content); put("created_at", timestamp)
        }
        writableDatabase.insert("chat_messages", null, values)
    }

    /** Returns the latest [limit] messages, oldest first. */
    fun getChats(limit: Int = 200): List<ChatMessage> {
        val list = ArrayList<ChatMessage>()
        readableDatabase.rawQuery(
            "SELECT id, role, content, created_at FROM chat_messages ORDER BY id DESC LIMIT ?",
            arrayOf(limit.toString())
        ).use { c ->
            while (c.moveToNext()) {
                list.add(ChatMessage(c.getLong(0), c.getString(1), c.getString(2), c.getLong(3)))
            }
        }
        list.reverse()
        return list
    }

    fun clearChats() {
        writableDatabase.delete("chat_messages", null, null)
    }

    // ---- quiz ----
    fun insertQuizResult(total: Int, correct: Int, takenAt: Long) {
        val values = ContentValues().apply {
            put("taken_at", takenAt); put("total", total); put("correct", correct)
        }
        writableDatabase.insert("quiz_results", null, values)
    }

    fun getQuizResults(limit: Int): List<QuizResult> {
        val list = ArrayList<QuizResult>()
        readableDatabase.rawQuery(
            "SELECT id, taken_at, total, correct FROM quiz_results ORDER BY id DESC LIMIT ?",
            arrayOf(limit.toString())
        ).use { c ->
            while (c.moveToNext()) {
                list.add(QuizResult(c.getLong(0), c.getLong(1), c.getInt(2), c.getInt(3)))
            }
        }
        return list
    }

    // ---- learning progress ----
    fun setTopicDone(topicId: String, done: Boolean) {
        val values = ContentValues().apply {
            put("topic_id", topicId)
            put("completed", if (done) 1 else 0)
            put("updated_at", System.currentTimeMillis())
        }
        writableDatabase.insertWithOnConflict("topic_progress", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun doneTopicIds(): Set<String> {
        val set = HashSet<String>()
        readableDatabase.rawQuery("SELECT topic_id FROM topic_progress WHERE completed = 1", null).use { c ->
            while (c.moveToNext()) set.add(c.getString(0))
        }
        return set
    }

    // ---- privacy ----
    fun clearAll() {
        writableDatabase.apply {
            delete("chat_messages", null, null)
            delete("quiz_results", null, null)
            delete("topic_progress", null, null)
        }
    }

    companion object {
        private const val DB_NAME = "nusuk.db"
        private const val DB_VERSION = 1
    }
}
