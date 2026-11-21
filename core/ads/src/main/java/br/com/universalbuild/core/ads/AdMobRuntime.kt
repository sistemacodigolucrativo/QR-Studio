package br.com.universalbuild.core.ads

import android.app.Activity
import android.os.SystemClock
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.rewarded.OnUserEarnedRewardListener
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardItem
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAd
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdEventCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Safe-by-default AdMob controller. Initialize only when UMP consent is current.
 * Ads are never requested while mode is off.
 */
object AdMobRuntime {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutableReady = MutableStateFlow(false)
    val ready = mutableReady.asStateFlow()
    private val mutablePrivacy = MutableStateFlow(false)
    val privacyOptionsRequired = mutablePrivacy.asStateFlow()

    private var config = AdMobConfig.off()
    private var initialized = false
    private var loadingInterstitial = false
    private var loadingRewarded = false
    private var interstitial: InterstitialAd? = null
    private var rewarded: RewardedAd? = null
    private var lastInterstitialAt = 0L

    val settings: AdMobConfig get() = config

    fun configure(value: AdMobConfig) {
        check(!initialized) { "Configure before SDK initialization" }
        config = value
    }

    fun start(activity: Activity) {
        if (!config.enabled || initialized) return
        val consent = UserMessagingPlatform.getConsentInformation(activity)
        consent.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            {
                mutablePrivacy.value =
                    consent.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    mutablePrivacy.value =
                        consent.privacyOptionsRequirementStatus ==
                        ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
                    if (consent.canRequestAds()) initializeSdk(activity)
                }
            },
            { mutableReady.value = false }
        )
    }

    fun showPrivacyOptions(activity: Activity) {
        if (mutablePrivacy.value) {
            UserMessagingPlatform.showPrivacyOptionsForm(activity) { }
        }
    }

    @Synchronized
    private fun initializeSdk(activity: Activity) {
        if (initialized) return
        initialized = true
        scope.launch {
            MobileAds.initialize(
                activity.applicationContext,
                InitializationConfig.Builder(config.appId).build()
            ) {
                mutableReady.value = true
                activity.runOnUiThread {
                    preloadInterstitial()
                    preloadRewarded()
                }
            }
        }
    }

    /**
     * Call at an explicit natural navigation break, never during active tasks.
     * Enforces the minimum interval configured in Universal Build.
     */
    fun showInterstitialAtNaturalBreak(activity: Activity): Boolean {
        if (!mutableReady.value || !config.interstitialEnabled) return false
        val now = SystemClock.elapsedRealtime()
        if (lastInterstitialAt != 0L &&
            now - lastInterstitialAt < config.interstitialCooldownSeconds * 1000L
        ) {
            return false
        }
        val ad = interstitial ?: return false
        interstitial = null
        ad.adEventCallback = object : InterstitialAdEventCallback {
            override fun onAdDismissedFullScreenContent() {
                activity.runOnUiThread { preloadInterstitial() }
            }

            override fun onAdFailedToShowFullScreenContent(error: FullScreenContentError) {
                activity.runOnUiThread { preloadInterstitial() }
            }
        }
        lastInterstitialAt = now
        ad.show(activity)
        return true
    }

    /**
     * Invoke only on explicit opt-in. Callback is NOT a verified coin credit.
     * A real rewards wallet must use server-side verification and idempotency.
     */
    fun showRewardedAfterOptIn(activity: Activity, onEarned: (Int, String) -> Unit): Boolean {
        if (!mutableReady.value || !config.rewardedEnabled) return false
        val ad = rewarded ?: return false
        rewarded = null
        ad.adEventCallback = object : RewardedAdEventCallback {
            override fun onAdDismissedFullScreenContent() {
                activity.runOnUiThread { preloadRewarded() }
            }

            override fun onAdFailedToShowFullScreenContent(error: FullScreenContentError) {
                activity.runOnUiThread { preloadRewarded() }
            }
        }
        val listener = object : OnUserEarnedRewardListener {
            override fun onUserEarnedReward(item: RewardItem) {
                activity.runOnUiThread { onEarned(item.amount, item.type) }
            }
        }
        ad.show(activity, listener)
        return true
    }

    fun preloadInterstitial() {
        if (!mutableReady.value || !config.interstitialEnabled ||
            loadingInterstitial || interstitial != null
        ) {
            return
        }
        loadingInterstitial = true
        InterstitialAd.load(
            AdRequest.Builder(config.interstitialId).build(),
            object : AdLoadCallback<InterstitialAd> {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitial = ad
                    loadingInterstitial = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitial = null
                    loadingInterstitial = false
                }
            }
        )
    }

    fun preloadRewarded() {
        if (!mutableReady.value || !config.rewardedEnabled ||
            loadingRewarded || rewarded != null
        ) {
            return
        }
        loadingRewarded = true
        RewardedAd.load(
            AdRequest.Builder(config.rewardedId).build(),
            object : AdLoadCallback<RewardedAd> {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewarded = ad
                    loadingRewarded = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewarded = null
                    loadingRewarded = false
                }
            }
        )
    }
}
