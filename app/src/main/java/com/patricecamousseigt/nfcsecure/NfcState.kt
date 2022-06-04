package com.patricecamousseigt.nfcsecure

import android.content.Context
import android.nfc.NfcAdapter

class NfcState(private val context: Context) {

    fun canBeEnabledOnThisPhone(): Boolean {
        val mNfcAdapter = NfcAdapter.getDefaultAdapter(context)
        return mNfcAdapter != null
    }

    fun isEnabled(): Boolean {
        val mNfcAdapter = NfcAdapter.getDefaultAdapter(context)
        return mNfcAdapter?.isEnabled ?: false
    }
}