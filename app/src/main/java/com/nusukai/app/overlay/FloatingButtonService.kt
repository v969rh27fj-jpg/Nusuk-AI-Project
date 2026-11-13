package com.nusukai.app.overlay

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.nusukai.app.MainActivity
import com.nusukai.app.R
import com.nusukai.app.util.LocaleHelper
import com.nusukai.app.util.Notifications
import com.nusukai.app.util.nusuk
import kotlin.math.abs

/**
 * Optional floating AI button.
 *
 * Rules this service follows:
 *  - It only starts after the user turned the switch on in Settings AND Android's
 *    "Display over other apps" permission was granted by the user.
 *  - It runs as a visible foreground service (with a notification that has a "Turn off" button).
 *  - It stops itself immediately if the permission is missing.
 */
class FloatingButtonService : Service() {

    private var windowManager: WindowManager? = null
    private var bubble: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            nusuk.settings.floatingEnabled = false
            shutDown()
            return START_NOT_STICKY
        }
        if (!Settings.canDrawOverlays(this)) {
            shutDown()
            return START_NOT_STICKY
        }
        startAsForeground()
        addBubble()
        return START_NOT_STICKY
    }

    private fun startAsForeground() {
        Notifications.ensureChannels(this)
        val loc = LocaleHelper.localizedContext(this, nusuk.settings.language)

        val stop = PendingIntent.getService(
            this, 1,
            Intent(this, FloatingButtonService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val open = PendingIntent.getActivity(
            this, 2, openChatIntent(this),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, Notifications.CHANNEL_FLOATING)
            .setSmallIcon(R.drawable.ic_nav_ai)
            .setContentTitle(loc.getString(R.string.float_notice_title))
            .setContentText(loc.getString(R.string.float_notice_text))
            .setContentIntent(open)
            .addAction(0, loc.getString(R.string.float_stop), stop)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        val type = if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
        ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, type)
    }

    private fun addBubble() {
        if (bubble != null) return
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager = wm

        val density = resources.displayMetrics.density
        val size = (56 * density).toInt()

        val view = TextView(this).apply {
            text = "AI"
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(context, R.color.brand_gold_light))
            typeface = Typeface.DEFAULT_BOLD
            textSize = 16f
            elevation = 8 * density
            contentDescription = LocaleHelper.localizedContext(context, nusuk.settings.language)
                .getString(R.string.float_button_desc)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(ContextCompat.getColor(context, R.color.brand_green))
                setStroke((3 * density).toInt(), ContextCompat.getColor(context, R.color.brand_gold))
            }
        }

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val params = WindowManager.LayoutParams(
            size, size, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (16 * density).toInt()
            y = (200 * density).toInt()
        }

        val slop = ViewConfiguration.get(this).scaledTouchSlop
        var startX = 0
        var startY = 0
        var touchX = 0f
        var touchY = 0f
        var dragged = false

        view.setOnClickListener { startActivity(openChatIntent(this)) }
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = params.x; startY = params.y
                    touchX = event.rawX; touchY = event.rawY
                    dragged = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - touchX).toInt()
                    val dy = (event.rawY - touchY).toInt()
                    if (abs(dx) > slop || abs(dy) > slop) dragged = true
                    if (dragged) {
                        params.x = startX + dx
                        params.y = startY + dy
                        wm.updateViewLayout(v, params)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!dragged) v.performClick()
                    true
                }
                else -> false
            }
        }

        wm.addView(view, params)
        bubble = view
    }

    private fun removeBubble() {
        bubble?.let { runCatching { windowManager?.removeView(it) } }
        bubble = null
    }

    private fun shutDown() {
        removeBubble()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        removeBubble()
        super.onDestroy()
    }

    companion object {
        private const val ACTION_STOP = "com.nusukai.app.action.STOP_FLOATING"
        private const val NOTIFICATION_ID = 1001

        private fun openChatIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_OPEN_CHAT, true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        /** Call only while the app is in the foreground (e.g. from Settings or MainActivity). */
        fun start(context: Context) {
            ContextCompat.startForegroundService(context, Intent(context, FloatingButtonService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FloatingButtonService::class.java))
        }
    }
}
