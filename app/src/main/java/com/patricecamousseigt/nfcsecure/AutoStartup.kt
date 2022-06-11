package com.patricecamousseigt.nfcsecure

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.lang.Exception
import android.os.Build
import com.patricecamousseigt.nfcsecure.repository.PrefRepository
import com.patricecamousseigt.nfcsecure.util.Util.Companion.TAG


class AutoStartup: BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        try {
            val intent = p1!!
            if (intent.action != null && intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                val context = p0!!
                val activated = PrefRepository(p0).getActivation()
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
            Log.e(TAG, "Error : $e")
        }
    }
}