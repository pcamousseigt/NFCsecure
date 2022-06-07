package com.patricecamousseigt.nfcsecure

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import java.lang.Exception
import com.google.android.ump.FormError

import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentInformation.OnConsentInfoUpdateFailureListener
import com.google.android.ump.ConsentInformation.OnConsentInfoUpdateSuccessListener

import com.google.android.ump.UserMessagingPlatform

import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.ConsentForm
import com.patricecamousseigt.nfcsecure.databinding.SettingsActivityBinding
import com.patricecamousseigt.nfcsecure.util.UtilConst
import com.patricecamousseigt.nfcsecure.util.UtilConst.Companion.TAG


class SettingsActivity : AppCompatActivity() {

    //private lateinit var bindingActivity: SettingsActivityBinding

    //private var consentInformation: ConsentInformation? = null
    //private var consentForm: ConsentForm? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bindingActivity = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(bindingActivity.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        }

        // Set tag for underage of consent. false means users are not underage.
        val params = ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()

        val consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(this, params,
            {
                // The consent information state was updated, now ready to check if a form is available
                if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    loadForm();
                }
            },
            {
                // Handle the error
                Log.e(TAG, "OnConsentInfoUpdateFailureListener : $it")
            })

        val dataUsageButton = bindingActivity.dataUsage
        dataUsageButton.setOnClickListener { loadForm() }
    }

    private fun loadForm() {
        UserMessagingPlatform.loadConsentForm(this, {
            consentForm ->
            //this@SettingsActivity.consentForm = consentForm
            consentForm.show(this@SettingsActivity) {
                // Handle dismissal
                Log.i(TAG, "Form dismissed")
            }
        }) {
            // Handle the error
            Log.e(TAG, "Form error : $it")
        }
    }
}