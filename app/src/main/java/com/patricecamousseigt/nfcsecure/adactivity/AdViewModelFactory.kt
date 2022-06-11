package com.patricecamousseigt.nfcsecure.adactivity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ViewModel provider factory to instantiate SettingsViewModel.
 * Required given SettingsViewModel has a non-empty constructor
 */
class AdViewModelFactory(private val app: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdViewModel::class.java)) {
            return AdViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}