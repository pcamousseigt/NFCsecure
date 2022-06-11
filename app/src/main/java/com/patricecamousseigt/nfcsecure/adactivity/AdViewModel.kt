package com.patricecamousseigt.nfcsecure.adactivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.patricecamousseigt.nfcsecure.repository.PrefRepository
import android.os.CountDownTimer
import com.patricecamousseigt.nfcsecure.R


class AdViewModel(val app: Application): AndroidViewModel(app) {

    companion object WaitingTime {
        const val WAITING_TIME_MIN = 5000L // 5 seconds
        const val WAITING_TIME_MAX = 10000L // 10 seconds
    }

    private val prefsRepository = PrefRepository(app)

    private val _waitingText = MutableLiveData<CharSequence>()
    val waitingText: LiveData<CharSequence> = _waitingText

    private val _nfcInspectorButtonEnabled = MutableLiveData<Boolean>()
    val nfcInspectorButtonEnabled: LiveData<Boolean> = _nfcInspectorButtonEnabled

    init {
        setWaitingText(app.getText(R.string.thanks_for_waiting))
        setNfcInspectorButtonEnabled(false)
    }

    fun setActivation(activation: Boolean) {
        prefsRepository.setActivation(activation)
    }

    fun setWaitingText(text: CharSequence) {
        _waitingText.postValue(text)
    }

    fun setNfcInspectorButtonEnabled(enable: Boolean) {
        _nfcInspectorButtonEnabled.postValue(enable)
    }

    private fun enableNfcButton(delay: Long, onTickEnabled: Boolean) {
        // force user to watch the image during five seconds
        val timer = object: CountDownTimer(delay, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (onTickEnabled) {
                    val secondsUntilFinished = millisUntilFinished.toInt() / 1000

                    setWaitingText(
                        app.getString(R.string.thanks_for_waiting)
                        .plus(" ")
                        .plus(app.resources.getQuantityString(R.plurals.x_seconds, secondsUntilFinished, secondsUntilFinished))
                    )
                }
            }

            override fun onFinish() {
                setNfcInspectorButtonEnabled(true)
            }
        }
        timer.start()
    }

    fun enableNfcButtonInMaxDelay() {
        enableNfcButton(WAITING_TIME_MAX, false)
    }

    fun enableNfcButton() {
        enableNfcButton(WAITING_TIME_MIN, true)
    }
}