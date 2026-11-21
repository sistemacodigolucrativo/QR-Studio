package br.com.universalbuild.core.ads

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdMobConfigTest {
    @Test
    fun defaultDisablesAllAds() {
        assertFalse(AdMobConfig.off().enabled)
    }

    @Test
    fun configuredBannerEnablesAds() {
        assertTrue(
            AdMobConfig(
                mode = "test",
                appId = "ca-app-pub-3940256099942544~3347511713",
                bannerEnabled = true,
                bannerId = "ca-app-pub-3940256099942544/9214589741",
                interstitialEnabled = false,
                interstitialId = "",
                rewardedEnabled = false,
                rewardedId = "",
                interstitialCooldownSeconds = 120
            ).enabled
        )
    }
}
