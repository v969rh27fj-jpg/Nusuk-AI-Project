package com.nusukai.app.data

import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/** Talks to YOUR backend (POST {baseUrl}/v1/chat). It never talks to an AI provider directly and holds no API key. */
object ChatApi {

    class ApiException(val error: ChatError, message: String) : Exception(message)

    data class ApiReply(val reply: String, val demo: Boolean)

    /** Blocking call. Run on a background dispatcher. */
    fun chat(baseUrl: String, history: List<ChatMessage>, language: String): ApiReply {
        val base = baseUrl.trim().trimEnd('/')
        if (!base.startsWith("https://", ignoreCase = true)) {
            throw ApiException(ChatError.SERVER, "Backend URL must use https://")
        }

        val messages = JSONArray()
        // Only the most recent messages are sent (keeps requests small and private).
        history.takeLast(MAX_HISTORY).forEach {
            messages.put(JSONObject().put("role", it.role).put("content", it.content.take(MAX_CHARS)))
        }
        val body = JSONObject().put("messages", messages).put("language", language)

        var conn: HttpURLConnection? = null
        try {
            conn = URL("$base/v1/chat").openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = 15_000
            conn.readTimeout = 45_000
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""

            return when {
                code in 200..299 -> {
                    val json = JSONObject(text)
                    ApiReply(json.getString("reply"), json.optString("mode") == "demo")
                }
                code == 429 -> throw ApiException(ChatError.RATE, "rate limited")
                else -> throw ApiException(ChatError.SERVER, "http $code")
            }
        } catch (e: ApiException) {
            throw e
        } catch (e: IOException) { // no internet, DNS failure, timeout, ...
            throw ApiException(ChatError.NETWORK, e.javaClass.simpleName)
        } catch (e: org.json.JSONException) {
            throw ApiException(ChatError.SERVER, "bad response")
        } finally {
            conn?.disconnect()
        }
    }

    private const val MAX_HISTORY = 12
    private const val MAX_CHARS = 2000
}
