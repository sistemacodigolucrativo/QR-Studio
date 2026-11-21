package br.com.universalbuild.core.ads

/** Validated build-time settings from the Universal Build console. */
data class AdMobConfig(
    val mode: String,
    val appId: String,
    val bannerEnabled: Boolean,
    val bannerId: String,
    val interstitialEnabled: Boolean,
    val interstitialId: String,
    val rewardedEnabled: Boolean,
    val rewardedId: String,
    val interstitialCooldownSeconds: Int
) {
    init {
        require(mode in setOf("off", "test", "live"))
        require(interstitialCooldownSeconds in 30..3600)
        if (mode != "off") {
            require(APP_ID.matches(appId))
            if (bannerEnabled) require(UNIT_ID.matches(bannerId))
            if (interstitialEnabled) require(UNIT_ID.matches(interstitialId))
            if (rewardedEnabled) require(UNIT_ID.matches(rewardedId))
        }
    }

    val enabled: Boolean
        get() = mode != "off" && (bannerEnabled || interstitialEnabled || rewardedEnabled)

    companion object {
        private val APP_ID = Regex("ca-app-pub-\\d{16}~\\d{10}")
        private val UNIT_ID = Regex("ca-app-pub-\\d{16}/\\d{10}")

        fun off(): AdMobConfig = AdMobConfig(
            mode = "off",
            appId = "",
            bannerEnabled = false,
            bannerId = "",
            interstitialEnabled = false,
            interstitialId = "",
            rewardedEnabled = false,
            rewardedId = "",
            interstitialCooldownSeconds = 120
        )
    }
}
