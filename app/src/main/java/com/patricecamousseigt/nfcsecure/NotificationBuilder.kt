package com.patricecamousseigt.nfcsecure

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat

class NotificationBuilder(private val context: Context) {

    enum class NotificationContent(val title: String, val body: String, val id: Int) {
        NOTIFICATION_SERVICE_RUNNING("NFCsecure", "Protecting you NFC.", 1000),
        NOTIFICATION_NFC("NFC enabled", "Click here to disable your NFC.", 1001);
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
            val channel = NotificationChannel(channelId, "NFCsecure Inspector", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // set the info for the views that show in the notification panel
        val b: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
        b.setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setContentIntent(pendingIntent)
            .setSmallIcon(image)

        return b.build()
    }

    fun createNotification(notification: NotificationContent) {
        // create builder and send the notification
        when (notification) {
            NotificationContent.NOTIFICATION_NFC -> {
                val built = builder(NotificationContent.NOTIFICATION_NFC)
                notificationManager.notify(NotificationContent.NOTIFICATION_NFC.id, built)
            }
            NotificationContent.NOTIFICATION_SERVICE_RUNNING -> {
                val built = builder(NotificationContent.NOTIFICATION_SERVICE_RUNNING)
                notificationManager.notify(NotificationContent.NOTIFICATION_SERVICE_RUNNING.id, built)
            }
        }
    }

}