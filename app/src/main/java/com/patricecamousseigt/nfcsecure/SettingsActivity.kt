package com.patricecamousseigt.nfcsecure

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import java.lang.Exception

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        }
    }

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
                        context?.getSharedPreferences(Const.NAME, MODE_PRIVATE)?.edit()?.putBoolean(Const.ACTIVATION, activation)?.apply()
                        // start NFC inspector service
                        activity?.startService(Intent(activity, NfcService::class.java))
                    }
                    else {
                        startActivity(Intent(activity, AdActivity::class.java))
                    }
                } catch (e: Exception) { Log.e("[NFCsecure]", "Error : $e") }
                true
            }

            val listPreference = findPreference<ListPreference>("duration")
            listPreference?.setOnPreferenceChangeListener { preference, newValue ->
                try {
                    val duration = (newValue as String).toInt()
                    // save the value in shared preferences
                    context?.getSharedPreferences(Const.NAME, MODE_PRIVATE)?.edit()?.putInt(Const.DURATION, duration)?.apply()
                } catch (e: Exception) { Log.e("[NFCsecure]", "Error : $e") }
                true
            }
        }

        override fun onResume() {
            super.onResume()
            try {
                val activation = context?.getSharedPreferences(Const.NAME, MODE_PRIVATE)?.getBoolean(Const.ACTIVATION, false)!!
                switchPreference?.isChecked = activation
            } catch (e: Exception) { Log.e("[NFCsecure]", "Error : $e") }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}