package br.com.universalbuild.core.ads

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError

/** A bottom-anchored adaptive banner, never drawn above functional content. */
@Composable
fun AdMobFooter(modifier: Modifier = Modifier) {
    val ready by AdMobRuntime.ready.collectAsState()
    val config = AdMobRuntime.settings
    if (!ready || !config.bannerEnabled) return
    val context = LocalContext.current
    val preview = LocalInspectionMode.current
    val width = (LocalConfiguration.current.screenWidthDp - 24).coerceAtLeast(1)
    val adView = remember(context) { AdView(context) }
    DisposableEffect(adView) { onDispose { adView.destroy() } }
    LaunchedEffect(adView, config.bannerId, width, preview) {
        if (!preview) {
            val size = AdSize.getLargeAnchoredAdaptiveBannerAdSize(context, width)
            val request = BannerAdRequest.Builder(config.bannerId, size).build()
            adView.loadAd(request, object : AdLoadCallback<BannerAd> {
                override fun onAdLoaded(ad: BannerAd) { }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w("UniversalAds", "Banner unavailable: " + error.message)
                }
            })
        }
    }
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        AndroidView(
            modifier = Modifier.wrapContentHeight(),
            factory = { adView }
        )
    }
}
