package com.patricecamousseigt.nfcsecure.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.patricecamousseigt.nfcsecure.adactivity.AdActivity
import com.patricecamousseigt.nfcsecure.NfcService
import com.patricecamousseigt.nfcsecure.R
import com.patricecamousseigt.nfcsecure.util.Util.Companion.TAG
import java.lang.Exception

class SettingsFragment : PreferenceFragmentCompat() {

    private val settingsViewModel by lazy {
        ViewModelProvider(this, SettingsViewModelFactory(requireActivity().application)).get(SettingsViewModel::class.java)
    }

    private var switchPreference: SwitchPreferenceCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        switchPreference = findPreference("activation")
        switchPreference?.setOnPreferenceChangeListener { _, newValue ->
            try {
                val activation = newValue as Boolean

                // launch service or stop it depending on user's choice
                if (activation) {
                    // save the value in shared preferences
                    settingsViewModel.setActivation(activation)

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
                settingsViewModel.setDuration(duration)
            } catch (e: Exception) { Log.e(TAG, "Error : $e") }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            val serviceRunning = NfcService().isRunning(requireContext())
            val switchChecked = settingsViewModel.getActivation()
            if (!serviceRunning && switchChecked) { settingsViewModel.setActivation(serviceRunning) }
            switchPreference?.isChecked = serviceRunning
        } catch (e: Exception) { Log.e(TAG, "Error : $e") }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }


}