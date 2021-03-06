package com.patricecamousseigt.nfcsecure.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.patricecamousseigt.nfcsecure.R
import com.patricecamousseigt.nfcsecure.main.SettingsActivity


class NotificationBuilder(private val context: Context) {

    enum class NotificationContent(val title: Int, val body: Int, val id: Int) {
        NOTIFICATION_SERVICE_RUNNING(R.string.app_name, R.string.protecting_nfc, 1000),
        NOTIFICATION_NFC(R.string.nfc_enabled, R.string.click_disable_nfc, 1001);
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun builder(notification: NotificationContent): Notification {

        val intent: Intent = when(notification) {
            NotificationContent.NOTIFICATION_SERVICE_RUNNING -> Intent(context, SettingsActivity::class.java)
            NotificationContent.NOTIFICATION_NFC -> Intent(Settings.ACTION_NFC_SETTINGS)
        }

        // the PendingIntent to launch our activity if the user selects this notification
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val image: Int = R.drawable.ic_notification

        val channelId = "NFC_NOTIFICATION"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "NFCsecure Inspector"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // set the info for the views that show in the notification panel
        val b: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
        b.setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(context.getString(notification.title))
            .setContentText(context.getString(notification.body))
            .setContentIntent(pendingIntent)
            .setSmallIcon(image)

        return b.build()
    }

    fun createNotification(notification: NotificationContent) {
        notificationManager.notify(notification.id, builder(notification))
    }

    fun cancelNotification() {
        for (nContent in NotificationContent.values()) {
            notificationManager.cancel(nContent.id)
        }
    }
}