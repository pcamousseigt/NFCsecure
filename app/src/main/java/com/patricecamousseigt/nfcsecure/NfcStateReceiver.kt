package com.patricecamousseigt.nfcsecure

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.util.Log

class NfcStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) {
            val state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF)
            when (state) {
                NfcAdapter.STATE_OFF -> { Log.i("[NFCsecure]", "Nfc state disabled.") }
                NfcAdapter.STATE_TURNING_OFF -> { Log.i("[NFCsecure]", "Nfc state turning off.") }
                NfcAdapter.STATE_ON -> { Log.i("[NFCsecure]", "Nfc state enabled.") }
                NfcAdapter.STATE_TURNING_ON -> {
                    // Do something
                    Log.i("[NFCsecure]", "Nfc state turning on.")
                    //applicationContext.getSharedPreferences().findPreference<SwitchPreferenceCompat>("activation")
                }
            }
        }
    }
}