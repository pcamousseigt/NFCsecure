package com.patricecamousseigt.nfcsecure

import android.app.Service
import android.content.*
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.nfc.NfcAdapter
import androidx.preference.SwitchPreferenceCompat


class NfcService: Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private val nfcStateReceiver = NfcStateReceiver()

    override fun onBind(p0: Intent?): IBinder? {
        Log.i("[NFCsecure]","Service onBind.")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("[NFCsecure]","Service running in the background.")

        // we need this wake lock so our service gets not affected by Doze Mode
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NfcService::lock").apply { acquire() }
        }

        // Register the broadcast receiver
        this.registerReceiver(nfcStateReceiver, IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED))

        // make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i("[NFCsecure]","Service onDestroy.")

        super.onDestroy()

        // Remove the broadcast listener
        this.unregisterReceiver(nfcStateReceiver);

        // release the wakelock
        wakeLock?.release()
    }

}