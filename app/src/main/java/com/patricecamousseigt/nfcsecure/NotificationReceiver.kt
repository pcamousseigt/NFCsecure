package com.patricecamousseigt.nfcsecure

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.patricecamousseigt.nfcsecure.NotificationBuilder.NotificationContent


class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.i("[NFCsecure]", "NotificationReceiver onReceive.")
        NotificationBuilder(context).createNotification(NotificationContent.NOTIFICATION_NFC)
    }


}