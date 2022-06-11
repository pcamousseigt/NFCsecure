package com.patricecamousseigt.nfcsecure.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ViewModel provider factory to instantiate SettingsViewModel.
 * Required given SettingsViewModel has a non-empty constructor
 */
class SettingsActivityViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsActivityViewModel::class.java)) {
            return SettingsActivityViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}