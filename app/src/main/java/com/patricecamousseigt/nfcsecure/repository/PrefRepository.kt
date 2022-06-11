package com.patricecamousseigt.nfcsecure.repository

import android.content.Context
import android.content.SharedPreferences
import com.patricecamousseigt.nfcsecure.repository.SharedPrefsConst.Companion.PREF_ACTIVATION
import com.patricecamousseigt.nfcsecure.repository.SharedPrefsConst.Companion.PREF_DURATION

class PrefRepository(val context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences(SharedPrefsConst.PREF_NAME, Context.MODE_PRIVATE)

    private val editor = pref.edit()

    fun getActivation(): Boolean {
        return PREF_ACTIVATION.getBoolean()
    }

    fun setActivation(activation: Boolean) {
        return PREF_ACTIVATION.put(activation)
    }

    fun getDuration(): Int {
        return PREF_DURATION.getInt()
    }

    fun setDuration(duration: Int) {
        return PREF_DURATION.put(duration)
    }

    private fun String.put(int: Int) {
        editor.putInt(this, int)
        editor.commit()
    }

    private fun String.put(boolean: Boolean) {
        editor.putBoolean(this, boolean)
        editor.commit()
    }

    private fun String.getInt() = pref.getInt(this, 0)

    private fun String.getBoolean() = pref.getBoolean(this, false)



}