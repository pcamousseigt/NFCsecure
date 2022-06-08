package com.patricecamousseigt.nfcsecure

import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.google.android.ump.*

import com.patricecamousseigt.nfcsecure.databinding.SettingsActivityBinding
import com.patricecamousseigt.nfcsecure.util.Util.Companion.TAG


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val bindingActivity = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(bindingActivity.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        }

        val dataUsageButton = bindingActivity.dataUsage

        // Set tag for underage of consent. false means users are not underage.
        val params = ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()

        val consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(this, params,
            {
                // The consent information state was updated, now ready to check if a form is available
                val consentStatus = consentInformation.consentStatus
                if (consentStatus != ConsentInformation.ConsentStatus.NOT_REQUIRED) {
                    // If the user is concerned by gdpr consent, the button to manage consent is displayed
                    dataUsageButton.setOnClickListener { loadForm() }
                    dataUsageButton.visibility = VISIBLE
                    dataUsageButton.isClickable = true
                    dataUsageButton.isFocusable = true
                    if (consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                        // If the user has not given his consent, the consent form is displayed
                        loadForm()
                    }
                }

            },
            {
                // Handle the error
                Log.e(TAG, "OnConsentInfoUpdateFailureListener : $it")
        })
    }

    private fun loadForm() {
        UserMessagingPlatform.loadConsentForm(this, { consentForm ->
            consentForm.show(this@SettingsActivity) {
                // Handle dismissal
                Log.i(TAG, "Consent form dismissed")
            }
        }) {
            // Handle the error
            Log.e(TAG, "Form error : $it")
        }
    }
}