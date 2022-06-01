package com.patricecamousseigt.nfcsecure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import java.lang.Exception
import kotlin.reflect.typeOf

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        }
    }


    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val switchPreference = findPreference<SwitchPreferenceCompat>("activation")
            switchPreference?.setOnPreferenceChangeListener { preference, newValue ->
                try {
                    val activation = newValue as Boolean
                    // save the value in shared preferences
                    context?.getSharedPreferences(Const.NAME, MODE_PRIVATE)?.edit()?.putBoolean(Const.ACTIVATION, activation)?.apply()
                    // launch service or stop it depending on user's choice
                    if (activation) { activity?.startService(Intent(activity, NfcService::class.java)) }
                    else { activity?.stopService(Intent(activity, NfcService::class.java)) }

                    Toast.makeText(context, activation.toString(), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) { Log.e("[NFCsecure]", "Error : $e") }
                true
            }

            val listPreference = findPreference<ListPreference>("duration")
            listPreference?.setOnPreferenceChangeListener { preference, newValue ->
                try {
                    val duration = (newValue as String).toInt()
                    // save the value in shared preferences
                    context?.getSharedPreferences(Const.NAME, Context.MODE_PRIVATE)?.edit()?.putInt(Const.DURATION, duration)?.apply()

                    Toast.makeText(context, duration.toString(), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) { Log.e("[NFCsecure]", "Error : $e") }
                true
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}