package com.patricecamousseigt.nfcsecure

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.patricecamousseigt.nfcsecure.util.Util.Companion.TAG
import java.lang.Exception

class SettingsFragment : PreferenceFragmentCompat() {

    private var switchPreference: SwitchPreferenceCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        switchPreference = findPreference("activation")
        Log.i(TAG, "NfcService isRunning : ${NfcService().isRunning(requireContext())}")
        switchPreference?.setOnPreferenceChangeListener { _, newValue ->
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
            } catch (e: Exception) { Log.e(TAG, "Error : $e") }
            true
        }

        val listPreference = findPreference<ListPreference>("duration")
        listPreference?.setOnPreferenceChangeListener { _, newValue ->
            try {
                val duration = (newValue as String).toInt()
                // save the value in shared preferences
                context?.getSharedPreferences(SharedPrefsConst.NAME, AppCompatActivity.MODE_PRIVATE)?.edit()?.putInt(SharedPrefsConst.DURATION, duration)?.apply()
            } catch (e: Exception) { Log.e(TAG, "Error : $e") }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            val serviceRunning = NfcService().isRunning(requireContext())
            var switchChecked = context?.getSharedPreferences(SharedPrefsConst.NAME,
                AppCompatActivity.MODE_PRIVATE)?.getBoolean(SharedPrefsConst.ACTIVATION, false)!!
            if (!serviceRunning && switchChecked) {
                switchChecked = serviceRunning
                context?.getSharedPreferences(SharedPrefsConst.NAME, AppCompatActivity.MODE_PRIVATE)
                    ?.edit()?.putBoolean(SharedPrefsConst.NAME, serviceRunning)?.apply()
            }
            switchPreference?.isChecked = switchChecked
        } catch (e: Exception) { Log.e(TAG, "Error : $e") }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }


}