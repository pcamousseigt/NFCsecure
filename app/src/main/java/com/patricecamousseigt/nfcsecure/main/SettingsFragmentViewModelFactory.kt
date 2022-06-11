package com.patricecamousseigt.nfcsecure.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ViewModel provider factory to instantiate SettingsViewModel.
 * Required given SettingsViewModel has a non-empty constructor
 */
class SettingsFragmentViewModelFactory(private val app: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsFragmentViewModel::class.java)) {
            return SettingsFragmentViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}