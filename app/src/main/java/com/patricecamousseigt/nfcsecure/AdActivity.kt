package com.patricecamousseigt.nfcsecure

import android.content.Intent
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.nativead.NativeAd
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAdView
import android.widget.*
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks
import com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.patricecamousseigt.nfcsecure.databinding.AdUnifiedBinding
import com.patricecamousseigt.nfcsecure.databinding.AdActivityBinding
import com.patricecamousseigt.nfcsecure.util.UtilConst.Companion.TAG
import java.util.*


class AdActivity : AppCompatActivity() {

    private lateinit var bindingActivity: AdActivityBinding

    private lateinit var bindingAdView: AdUnifiedBinding

    private lateinit var textViewWaiting: TextView

    private lateinit var disableNfcInspectorButton: Button

    private val ADMOB_AD_UNIT_ID = "ca-app-pub-6749482233379426/7627036906"

    private var nativeAd: NativeAd? = null

    private val WAITING_TIME = 5000 // 5 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingActivity = AdActivityBinding.inflate(layoutInflater)
        setContentView(bindingActivity.root)

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) { }

        // loads the ad
        refreshAd()

        textViewWaiting = bindingActivity.textWaiting
        textViewWaiting.text = getText(R.string.thanks_for_waiting)

        bindingActivity.back.setOnClickListener { finish() }

        // Create the disabling nfc button
        disableNfcInspectorButton = bindingActivity.disableNfc
        disableNfcInspectorButton.isEnabled = false
        disableNfcInspectorButton.setOnClickListener {
            // save the value in shared preferences
            getSharedPreferences(SharedPrefsConst.NAME, MODE_PRIVATE)?.edit()?.putBoolean(SharedPrefsConst.ACTIVATION, false)?.apply()
            // stop the NFC inspector service
            stopService(Intent(this, NfcService::class.java))
            // remove all notifications displayed on the status bar
            NotificationBuilder(this).cancelNotification()
            // stops the ad activity
            finish()
        }
    }

    private fun enableNfcButton() {
        // force user to watch the image during five seconds
        val timer = object: CountDownTimer(WAITING_TIME.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                runOnUiThread {
                    val secondsUntilFinished = millisUntilFinished.toInt() / 1000
                    textViewWaiting.text = getString(R.string.thanks_for_waiting) +
                            " " +
                            resources.getQuantityString(R.plurals.x_seconds, secondsUntilFinished, secondsUntilFinished)
                }
            }

            override fun onFinish() {
                runOnUiThread {
                    bindingActivity.layoutWaiting.visibility = View.INVISIBLE
                    disableNfcInspectorButton.isEnabled = true
                }
            }
        }
        timer.start()
    }

    /**
     * Populates a [NativeAdView] object with data from a given [NativeAd].
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView the view to be populated
     */
    private fun populateNativeAdView(nativeAd: NativeAd) {
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
            enableNfcButton()
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
        } else {
            // Ad does not contain a video asset
            enableNfcButton()
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     */
    private fun refreshAd() {
        val builder = AdLoader.Builder(this, ADMOB_AD_UNIT_ID)
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
                val adView = bindingAdView.root
                populateNativeAdView(nativeAd)
                frameLayout.removeAllViews()
                frameLayout.addView(adView)
            })

        val videoOptions = VideoOptions.Builder().setStartMuted(false).build()
        val adOptions: NativeAdOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)
        // ad loader
        val adLoader = builder.withAdListener(
            object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    enableNfcButton()
                    val error = String.format("domain: %s, code: %d, message: %s", loadAdError.domain, loadAdError.code, loadAdError.message)
                    Log.e(TAG, "Failed to load native ad with error $error")
                }
            }).build()
        // ad request
        val adRequest = AdRequest.Builder()
        // TODO : depending on consent form GDPR
        adLoader.loadAd(adRequest.build())
    }

    override fun onDestroy() {
        nativeAd?.destroy()
        super.onDestroy()
    }
}