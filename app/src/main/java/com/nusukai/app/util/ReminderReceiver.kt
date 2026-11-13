package com.nusukai.app.util

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.nusukai.app.MainActivity
import com.nusukai.app.R
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val app = context.nusuk
        if (!app.settings.dailyReminder) return

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            ReminderScheduler.schedule(context) // alarms are cleared on reboot
            return
        }

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val lang = app.settings.language
        val topic = app.content.dailyTopic(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) ?: return
        val localized = LocaleHelper.localizedContext(context, lang)
        Notifications.ensureChannels(context)

        val open = PendingIntent.getActivity(
            context, 10,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, Notifications.CHANNEL_DAILY)
            .setSmallIcon(R.drawable.ic_nav_ai)
            .setContentTitle(localized.getString(R.string.notif_daily_title))
            .setContentText(topic.title.pick(lang))
            .setStyle(NotificationCompat.BigTextStyle().bigText(topic.title.pick(lang) + ": " + topic.summary.pick(lang)))
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(2001, notification)
    }
}
