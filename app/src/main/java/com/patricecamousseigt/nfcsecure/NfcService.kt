package com.patricecamousseigt.nfcsecure

import android.app.Service
import android.content.*
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.nfc.NfcAdapter


class NfcService: Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var nfcStateReceiver: NfcStateReceiver? = null

    override fun onBind(p0: Intent?): IBinder? {
        Log.i("[NFCsecure]","Service onBind.")
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.i("[NFCsecure]","Service running in the background.")

        nfcStateReceiver = NfcStateReceiver()

        // we need this wake lock so our service gets not affected by Doze Mode
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NfcService::lock").apply { acquire() }
        }

        // register the broadcast receiver
        registerReceiver(nfcStateReceiver, IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED))

        val nfcEnabled = NfcState(applicationContext).isEnabled()
        if(nfcEnabled) {
            // launch manually the receiver if the nfc is already enabled
            nfcStateReceiver?.execute(applicationContext, NfcAdapter.STATE_ON)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i("[NFCsecure]","Service destroyed.")

        try {
            // remove the broadcast listener
            unregisterReceiver(nfcStateReceiver)
        } catch (e: Exception) {
            Log.e("[NFCsecure]", "Error : $e")
        }

        // remove manually a potential alarm scheduled
        nfcStateReceiver?.cancelNotificationIntent(applicationContext)

        // release the wakelock
        wakeLock?.release()

        super.onDestroy()
    }

}