package com.patricecamousseigt.nfcsecure

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.util.Log
import android.app.AlarmManager
import android.app.PendingIntent
import com.patricecamousseigt.nfcsecure.notification.NotificationReceiver
import com.patricecamousseigt.nfcsecure.repository.PrefRepository
import com.patricecamousseigt.nfcsecure.util.Util.Companion.TAG

class NfcStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) {
            val state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF)
            execute(context, state)
        }
    }

    fun execute(context: Context?, state : Int) {
        when (state) {
            NfcAdapter.STATE_OFF -> { Log.i(TAG, "Nfc state disabled.") }
            NfcAdapter.STATE_TURNING_OFF -> { Log.i(TAG, "Nfc state turning off.") }
            NfcAdapter.STATE_TURNING_ON -> { Log.i(TAG, "Nfc state turning on.") }
            NfcAdapter.STATE_ON -> { Log.i(TAG, "Nfc state enabled.")
                try {
                    val duration = PrefRepository(context!!).getDuration()
                    createNotificationIntent(context, duration)
                } catch (e: Exception) { Log.e(TAG, "Error: $e.") }
            }
        }
    }

    private fun createNotificationIntent(context: Context, duration: Int) {
        val pendingIntent = PendingIntent.getBroadcast(context, 1,
            Intent(context, NotificationReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + (duration.toLong() * 1000), pendingIntent)
    }

    fun cancelNotificationIntent(context: Context) {
        val pendingIntent = PendingIntent.getBroadcast(context, 1,
            Intent(context, NotificationReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

    }

}