package com.patricecamousseigt.nfcsecure.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SettingsActivityViewModel: ViewModel() {

    private val _dataUsage = MutableLiveData<ConsentInformationState>()
    val dataUsage: LiveData<ConsentInformationState> = _dataUsage

    fun setGdprRequired(required: Boolean) {
        _dataUsage.postValue(ConsentInformationState(required))
    }

}