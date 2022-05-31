package com.patricecamousseigt.nfcsecure

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.R.id
import android.app.NotificationManager
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import android.app.Notification
import android.app.PendingIntent
import android.content.SharedPreferences
import android.provider.Settings
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.preference.PreferenceManager
import com.patricecamousseigt.nfcsecure.Const.SharedPreferences.SONG_ACTIVATION
import com.patricecamousseigt.nfcsecure.Const.SharedPreferences.VIBRATION_ACTIVATION
import java.lang.Exception
import android.app.NotificationChannel
import android.os.Build


class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.i("[NFCsecure]", "NotificationReceiver onReceive.")
        createNotification(context)
    }

    private fun createNotification(context: Context) {
        val contentIntent = PendingIntent.getActivity(context,0,
            Intent(Settings.ACTION_NFC_SETTINGS), PendingIntent.FLAG_UPDATE_CURRENT)

        val b: NotificationCompat.Builder = NotificationCompat.Builder(context, "")

        val image: Int = R.drawable.ic_launcher_foreground
        var colorTitle: Int = R.color.black

        b.setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(VISIBILITY_PUBLIC)
            .setContentTitle("Attention : your NFC is enabled.")
            .setContentText("Turn off your NFC.")
            .setContentIntent(contentIntent)
            .setSmallIcon(image)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("NFC_NOTIFICATION", "Channel NFCsecure",
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            b.setChannelId("NFC_NOTIFICATION")
        }

        // TODO : manage vibration and song preferences

        notificationManager.notify(0, b.build())
    }
}