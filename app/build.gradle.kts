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
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
        }
        create("prod") { dimension = "environment" }
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
