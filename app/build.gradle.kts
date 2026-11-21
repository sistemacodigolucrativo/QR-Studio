import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

val config = Properties().apply {
    rootProject.file("app-config.properties").inputStream().use { load(it) }
}
val signingValues = listOf(
    "ANDROID_KEYSTORE_PATH",
    "ANDROID_KEYSTORE_PASSWORD",
    "ANDROID_KEY_ALIAS",
    "ANDROID_KEY_PASSWORD"
).associateWith { System.getenv(it) }
val canSign = signingValues.values.all { !it.isNullOrBlank() }

val universalAdsFile = rootProject.file("config/universal-build-admob.json")
val universalAdsData: Map<*, *>? = if (universalAdsFile.exists()) {
    val parsed = groovy.json.JsonSlurper().parse(universalAdsFile)
    parsed as? Map<*, *> ?: throw GradleException("AdMob: configuracao deve ser objeto JSON.")
} else {
    null
}
if (universalAdsData != null && (universalAdsData["version"] as? Number)?.toInt() != 1) {
    throw GradleException("AdMob: versao do contrato nao suportada.")
}
val universalAdsMode = universalAdsData?.get("mode") as? String ?: "off"
if (universalAdsMode !in setOf("off", "test", "live")) {
    throw GradleException("AdMob: modo invalido.")
}
val universalAdsAppId = universalAdsData?.get("appId") as? String ?: ""
val universalAdsUnits = universalAdsData?.get("units") as? Map<*, *> ?: emptyMap<String, String>()
val universalAdsFlags = universalAdsData?.get("enabled") as? Map<*, *> ?: emptyMap<String, Boolean>()
val universalAdsCooldown = (universalAdsData?.get("interstitialCooldownSeconds") as? Number)?.toInt() ?: 120
val universalAdsFormats = listOf("banner", "interstitial", "rewarded")
val universalAdsIdPattern = Regex("ca-app-pub-\\d{16}~\\d{10}")
val universalAdsUnitPattern = Regex("ca-app-pub-\\d{16}/\\d{10}")
val universalAdsDemoAppId = "ca-app-pub-3940256099942544~3347511713"
val universalAdsDemo = mapOf(
    "banner" to "ca-app-pub-3940256099942544/9214589741",
    "interstitial" to "ca-app-pub-3940256099942544/1033173712",
    "rewarded" to "ca-app-pub-3940256099942544/5224354917"
)
if (universalAdsCooldown !in 30..3600) {
    throw GradleException("AdMob: intervalo de intersticial invalido.")
}
if (universalAdsAppId.isNotEmpty() && !universalAdsIdPattern.matches(universalAdsAppId)) {
    throw GradleException("AdMob: App ID invalido.")
}
for (format in universalAdsFormats) {
    val unit = universalAdsUnits[format] as? String ?: ""
    if (unit.isNotEmpty() && !universalAdsUnitPattern.matches(unit)) {
        throw GradleException("AdMob: unidade invalida para " + format)
    }
    if (universalAdsFlags[format] != null && universalAdsFlags[format] !is Boolean) {
        throw GradleException("AdMob: chave enabled invalida para " + format)
    }
}
if (universalAdsMode == "live") {
    if (universalAdsAppId.isBlank() || universalAdsAppId == universalAdsDemoAppId) {
        throw GradleException("AdMob: producao requer App ID real.")
    }
    if (universalAdsFormats.none { universalAdsFlags[it] == true }) {
        throw GradleException("AdMob: producao requer pelo menos um formato ativo.")
    }
    for (format in universalAdsFormats) {
        if (universalAdsFlags[format] == true) {
            val unit = universalAdsUnits[format] as? String ?: ""
            if (!universalAdsUnitPattern.matches(unit) || unit in universalAdsDemo.values) {
                throw GradleException("AdMob: producao requer unidade real para " + format)
            }
        }
    }
}
val configureUniversalAds: (com.android.build.api.dsl.ApplicationProductFlavor, Boolean) -> Unit =
    { flavor, production ->
        val live = production && universalAdsMode == "live"
        val effectiveMode = if (production) {
            if (live) "live" else "off"
        } else {
            if (universalAdsMode == "off") "off" else "test"
        }
        val effectiveAppId = if (live) universalAdsAppId else universalAdsDemoAppId
        flavor.resValue("string", "ub_admob_app_id", effectiveAppId)
        flavor.buildConfigField("String", "ADS_MODE", "\"" + effectiveMode + "\"")
        flavor.buildConfigField("String", "ADS_APP_ID", "\"" + effectiveAppId + "\"")
        flavor.buildConfigField("int", "ADS_COOLDOWN_SECONDS", universalAdsCooldown.toString())
        for (format in universalAdsFormats) {
            val enabled = effectiveMode != "off" && universalAdsFlags[format] == true
            val unit = if (live) (universalAdsUnits[format] as? String ?: "") else universalAdsDemo.getValue(format)
            flavor.buildConfigField("boolean", "ADS_" + format.uppercase() + "_ENABLED", enabled.toString())
            flavor.buildConfigField("String", "ADS_" + format.uppercase() + "_ID", "\"" + unit + "\"")
        }
    }

android {
    namespace = "br.com.universalbuild.kotlinmaster"
    compileSdk = config.getProperty("compileSdk").toInt()
    defaultConfig {
        applicationId = config.getProperty("applicationId")
        minSdk = config.getProperty("minSdk").toInt()
        targetSdk = config.getProperty("targetSdk").toInt()
        versionCode = config.getProperty("versionCode").toInt()
        versionName = config.getProperty("versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resValue("string", "app_name", config.getProperty("appName"))
        resValue("color", "splash_background", config.getProperty("brandColor"))
        manifestPlaceholders["appIcon"] = "@mipmap/" + config.getProperty("iconName")
        buildConfigField("String", "BASE_API_URL", "\"" + config.getProperty("baseApiUrl") + "\"")
        buildConfigField("String", "BRAND_COLOR", "\"" + config.getProperty("brandColor") + "\"")
    }
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            configureUniversalAds(this, false)
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            configureUniversalAds(this, false)
        }
        create("prod") {
            dimension = "environment"
            configureUniversalAds(this, true)
        }
    }
    signingConfigs {
        if (canSign) {
            create("release") {
                storeFile = file(signingValues.getValue("ANDROID_KEYSTORE_PATH")!!)
                storePassword = signingValues.getValue("ANDROID_KEYSTORE_PASSWORD")
                keyAlias = signingValues.getValue("ANDROID_KEY_ALIAS")
                keyPassword = signingValues.getValue("ANDROID_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (canSign) signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") { isDebuggable = true }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}
kotlin { jvmToolchain(17) }

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:ads"))
    implementation(project(":feature:home"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.sizeclass)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}
tasks.named("preBuild").configure { dependsOn(rootProject.tasks.named("validateAppConfig")) }

detekt {
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}
