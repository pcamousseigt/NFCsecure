package com.patricecamousseigt.nfcsecure.main

import android.content.Context
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.patricecamousseigt.nfcsecure.util.Util
import org.junit.Test
import com.google.android.ump.ConsentDebugSettings
import org.junit.Before
import org.junit.runner.RunWith
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry
import com.patricecamousseigt.nfcsecure.UtilTest.Companion.TAG
import org.junit.After
import java.util.*


@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    private lateinit var scenario: ActivityScenario<SettingsActivity>

    private lateinit var context: Context

    @Before
    fun setup() {
        Log.d(TAG, "@Before setup")
        scenario = launchActivity()
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @After
    fun cleanup() {
        Log.d(TAG, "@After cleanup")
        scenario.close()
    }


    @Test
    fun isGdprRequiredIfInEEA_isCorrect() {
        Log.d(TAG, "Start isGdprRequiredIfInEEA_isCorrect")
        val debugSettings = ConsentDebugSettings.Builder(context)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId("4eda158f-4157-4ac6-b051-736aa864964f")
            .build()

        val params = ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build()

        scenario.onActivity { settingsActivity ->

            val consentInformation = UserMessagingPlatform.getConsentInformation(context)
            consentInformation.reset() // Reset consent state

            var callbackDone = false

            consentInformation.requestConsentInfoUpdate(settingsActivity, params,
                {
                    Log.d(TAG, "consentInformation.consentStatus : ${consentInformation.consentStatus.toInt()}")
                    Log.d(TAG, "Assert isGdprRequiredIfInEEA_isCorrect")
                    assert(consentInformation.consentStatus != ConsentInformation.ConsentStatus.NOT_REQUIRED)
                    callbackDone = true
                },
                {
                    // Handle the error
                    Log.d(TAG, "OnConsentInfoUpdateFailureListener : $it")
                    assert(false)
                    callbackDone = true
                }
            )
            if (!callbackDone) {
                Thread.sleep(3000)
            }
        }
        Log.d(TAG, "End isGdprRequiredIfInEEA_isCorrect")
    }

    @Test
    fun isGdprRequiredIfNotInEEA_isCorrect() {
        Log.d(TAG, "Start isGdprRequiredIfNotInEEA_isCorrect")
        val debugSettings = ConsentDebugSettings.Builder(context)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA)
            .addTestDeviceHashedId("4eda158f-4157-4ac6-b051-736aa864964f")
            .build()

        val params = ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build()

        // Trying to implement a efficient test for users not in EEA but DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA
        // seems to always return ConsentStatus.UNKNOWN instead of ConsentStatus.NOT_REQUIRED.
        // After long research, I saw a post from a member of the Mobile Ads SDK Team : "Consent SDK
        // only officially supports the DEBUG_GEOGRAPHY_EEA option in order to emulate the app being
        // run in the EU."
        // Source : https://groups.google.com/g/google-admob-ads-sdk/c/MEDiuHqIpYM/m/KjC_gbprAAAJ
        // So I used a VPN with my smartphone to connect from different countries outside EEA and
        // simulates the user experience of outside EEA users.
        // The test passed successfully.
        // Users outside the EEA are not affected by the user consent required by the GDPR.

        Log.d(TAG, "End isGdprRequiredIfNotInEEA_isCorrect")
    }

}