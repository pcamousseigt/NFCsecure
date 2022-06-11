package com.patricecamousseigt.nfcsecure.adactivity

import android.content.Intent
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.nativead.NativeAd
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAdView
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks
import com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.patricecamousseigt.nfcsecure.GdprConsentManager
import com.patricecamousseigt.nfcsecure.NfcService
import com.patricecamousseigt.nfcsecure.notification.NotificationBuilder
import com.patricecamousseigt.nfcsecure.databinding.AdUnifiedBinding
import com.patricecamousseigt.nfcsecure.databinding.AdActivityBinding
import com.patricecamousseigt.nfcsecure.util.Util.Companion.TAG
import androidx.lifecycle.Observer
import java.util.*


class AdActivity : AppCompatActivity() {

    private val adViewModel by lazy {
        ViewModelProvider(this, AdViewModelFactory(application)).get(AdViewModel::class.java)
    }

    private lateinit var bindingActivity: AdActivityBinding

    private lateinit var bindingAdView: AdUnifiedBinding

    private var nativeAd: NativeAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingActivity = AdActivityBinding.inflate(layoutInflater)
        setContentView(bindingActivity.root)

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) { }

        // loads the ad
        refreshAd()

        // enable the nfc button after 10 sec to set a maximum waiting time for the user
        adViewModel.enableNfcButtonInMaxDelay()

        // init the back button
        bindingActivity.back.setOnClickListener { finish() }

        // The waiting text above the button
        val textViewWaiting = bindingActivity.textWaiting
        adViewModel.waitingText.observe(this@AdActivity, Observer {
            val waitingText = it ?: return@Observer

            textViewWaiting.text = waitingText
        })

        // Create the disabling nfc button
        val disableNfcInspectorButton = bindingActivity.disableNfc
        adViewModel.nfcInspectorButtonEnabled.observe(this@AdActivity, Observer {
            val enabled = it ?: return@Observer

            disableNfcInspectorButton.isEnabled = enabled

            if (enabled) { bindingActivity.layoutWaiting.visibility = View.INVISIBLE }
        })
        disableNfcInspectorButton.setOnClickListener {
            // save the value in shared preferences
            adViewModel.setActivation(false)
            // stop the NFC inspector service
            stopService(Intent(this, NfcService::class.java))
            // remove all notifications displayed on the status bar
            NotificationBuilder(this).cancelNotification()
            // stops the ad activity
            finish()
        }
    }

    /**
     * Populates a [NativeAdView] object with data from a given [NativeAd].
     *
     * @param nativeAd the object containing the ad's assets
     */
    private fun populateNativeAdView(nativeAd: NativeAd): Boolean {
        // Set the media view.
        bindingAdView.root.mediaView = bindingAdView.adMedia

        // Set other ad assets.
        bindingAdView.root.headlineView = bindingAdView.adHeadline
        bindingAdView.root.bodyView = bindingAdView.adBody
        bindingAdView.root.callToActionView = bindingAdView.adCallToAction
        bindingAdView.root.iconView = bindingAdView.adAppIcon
        bindingAdView.root.priceView = bindingAdView.adPrice
        bindingAdView.root.starRatingView = bindingAdView.adStars
        bindingAdView.root.storeView = bindingAdView.adStore
        bindingAdView.root.advertiserView = bindingAdView.adAdvertiser

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        (bindingAdView.root.headlineView as TextView).text = nativeAd.headline
        bindingAdView.root.mediaView!!.setMediaContent(nativeAd.mediaContent!!)

        // These assets aren't guaranteed to be in every NativeAd so it's important to check before trying to display them.
        if (nativeAd.body == null) {
            bindingAdView.root.bodyView!!.visibility = View.INVISIBLE
        } else {
            bindingAdView.root.bodyView!!.visibility = View.VISIBLE
            (bindingAdView.root.bodyView as TextView?)!!.text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            bindingAdView.root.callToActionView!!.visibility = View.INVISIBLE
        } else {
            bindingAdView.root.callToActionView!!.visibility = View.VISIBLE
            (bindingAdView.root.callToActionView as Button?)!!.text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            bindingAdView.root.iconView!!.visibility = View.GONE
        } else {
            (bindingAdView.root.iconView as ImageView?)?.setImageDrawable(nativeAd.icon!!.drawable)
            bindingAdView.root.iconView!!.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            bindingAdView.root.priceView!!.visibility = View.INVISIBLE
        } else {
            bindingAdView.root.priceView!!.visibility = View.VISIBLE
            (bindingAdView.root.priceView as TextView?)!!.text = nativeAd.price
        }
        if (nativeAd.store == null) {
            bindingAdView.root.storeView!!.visibility = View.INVISIBLE
        } else {
            bindingAdView.root.storeView!!.visibility = View.VISIBLE
            (bindingAdView.root.storeView as TextView?)!!.text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            bindingAdView.root.starRatingView!!.visibility = View.INVISIBLE
        } else {
            (bindingAdView.root.starRatingView as RatingBar?)?.rating = nativeAd.starRating!!.toFloat()
            bindingAdView.root.starRatingView!!.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            bindingAdView.root.advertiserView!!.visibility = View.INVISIBLE
        } else {
            (bindingAdView.root.advertiserView as TextView?)!!.text = nativeAd.advertiser
            bindingAdView.root.advertiserView!!.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your native ad view with this native ad.
        bindingAdView.root.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't have a video asset.
        val vc = nativeAd.mediaContent!!.videoController
        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController.
            // The VideoController will call methods on this object when events occur in the video lifecycle.
            vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    // Video playback has ended
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd()
                }
            }
            return true
        }
        return false
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     */
    private fun refreshAd() {
        val admobAdUnitId = "ca-app-pub-6749482233379426/7627036906"
        val builder = AdLoader.Builder(this, admobAdUnitId)
        builder.forNativeAd(OnNativeAdLoadedListener {
                nativeAd ->
                // If this callback occurs after the activity is destroyed, you must call destroy and return or you may get a memory leak.
                if (isDestroyed || isFinishing || isChangingConfigurations) {
                    nativeAd.destroy()
                    return@OnNativeAdLoadedListener
                }
                // You must call destroy on old ads when you are done with them, otherwise you will have a memory leak.
                this@AdActivity.nativeAd?.destroy()
                this@AdActivity.nativeAd = nativeAd
                val frameLayout = bindingActivity.flAdPlaceholder
                bindingAdView = AdUnifiedBinding.inflate(layoutInflater)
                populateNativeAdView(nativeAd)
                frameLayout.removeAllViews()
                frameLayout.addView(bindingAdView.root)
                adViewModel.enableNfcButton()
            })

        val videoOptions = VideoOptions.Builder().setStartMuted(false).build()
        val adOptions: NativeAdOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)

        // ad loader
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adViewModel.enableNfcButton()
                val error = String.format("domain: %s, code: %d, message: %s", loadAdError.domain, loadAdError.code, loadAdError.message)
                Log.e(TAG, "Failed to load native ad with error $error")
            }
        }).build()

        // ad request
        val adRequest = AdRequest.Builder()

        val gdprConsent = GdprConsentManager(applicationContext)
        if (gdprConsent.canShowPersonalizedAds()) {
            // The default behavior of the Google Mobile Ads SDK is to serve personalized ads.
            // If a user has consented to receive only non-personalized ads, you can configure
            // an AdRequest object with the following code to specify that only non-personalized
            // ads should be returned.
            Log.i(TAG, "User consented to personalized ads")
        } else {
            // By default, the user consented to show non personalized ads
            Log.i(TAG, "User consented to non personalized ads")
            val extras = Bundle()
            extras.putString("npa", "1")
            adRequest.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        }
        adLoader.loadAd(adRequest.build())
    }

    override fun onDestroy() {
        nativeAd?.destroy()
        super.onDestroy()
    }
}