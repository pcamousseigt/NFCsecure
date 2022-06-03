package com.patricecamousseigt.nfcsecure

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import android.os.Build




class AutoStartup: BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        try {
            val intent = p1!!
            if (intent.action != null && intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                val context = p0!!
                val sharedPref = context.getSharedPreferences(Const.NAME, AppCompatActivity.MODE_PRIVATE)
                val activated = sharedPref.getBoolean(Const.ACTIVATION, false)
                if (activated) {
                    val nfcIntent = Intent(context, NfcService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(nfcIntent)
                    } else {
                        context.startService(nfcIntent)
                    }
                }
            }
        } catch (e: Exception) {
            Log.i("[NFCsecure]", "Error : $e")
        }
    }
}