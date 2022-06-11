package com.patricecamousseigt.nfcsecure.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.patricecamousseigt.nfcsecure.repository.PrefRepository


class SettingsFragmentViewModel(val app: Application): AndroidViewModel(app) {

    private val prefsRepository = PrefRepository(app)

    fun getActivation(): Boolean {
        return prefsRepository.getActivation()
    }

    fun setActivation(activation: Boolean) {
        prefsRepository.setActivation(activation)
    }

    fun setDuration(duration: Int) {
        prefsRepository.setDuration(duration)
    }
}