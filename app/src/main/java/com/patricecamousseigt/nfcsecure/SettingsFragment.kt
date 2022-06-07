package com.patricecamousseigt.nfcsecure

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.patricecamousseigt.nfcsecure.util.UtilConst
import java.lang.Exception

class SettingsFragment : PreferenceFragmentCompat() {

    private var switchPreference: SwitchPreferenceCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        switchPreference = findPreference<SwitchPreferenceCompat>("activation")
        switchPreference?.setOnPreferenceChangeListener { preference, newValue ->
            try {
                val activation = newValue as Boolean
                // launch service or stop it depending on user's choice
                if (activation) {
                    // save the value in shared preferences
                    context?.getSharedPreferences(SharedPrefsConst.NAME,
                        AppCompatActivity.MODE_PRIVATE
                    )?.edit()?.putBoolean(SharedPrefsConst.ACTIVATION, activation)?.apply()
                    // start NFC inspector service
                    activity?.startService(Intent(activity, NfcService::class.java))
                }
                else {
                    startActivity(Intent(activity, AdActivity::class.java))
                }
            } catch (e: Exception) { Log.e(UtilConst.TAG, "Error : $e") }
            true
        }

        val listPreference = findPreference<ListPreference>("duration")
        listPreference?.setOnPreferenceChangeListener { preference, newValue ->
            try {
                val duration = (newValue as String).toInt()
                // save the value in shared preferences
                context?.getSharedPreferences(SharedPrefsConst.NAME, AppCompatActivity.MODE_PRIVATE)?.edit()?.putInt(SharedPrefsConst.DURATION, duration)?.apply()
            } catch (e: Exception) { Log.e(UtilConst.TAG, "Error : $e") }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            val activation = context?.getSharedPreferences(SharedPrefsConst.NAME,
                AppCompatActivity.MODE_PRIVATE
            )?.getBoolean(SharedPrefsConst.ACTIVATION, false)!!
            switchPreference?.isChecked = activation
        } catch (e: Exception) { Log.e(UtilConst.TAG, "Error : $e") }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}