package com.patricecamousseigt.nfcsecure

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*

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

            val switchPreferenceCompat = findPreference<SwitchPreferenceCompat>("activation")
            switchPreferenceCompat?.setOnPreferenceChangeListener { preference, newValue ->
                Toast.makeText(context, newValue.toString(), Toast.LENGTH_SHORT).show()
                true
            }

            val listPreference = findPreference<ListPreference>("duration")
            listPreference?.setOnPreferenceChangeListener { preference, newValue ->
                Toast.makeText(context, newValue.toString(), Toast.LENGTH_SHORT).show()
                true
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}