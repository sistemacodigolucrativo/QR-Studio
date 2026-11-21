import java.util.Properties
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}
val appConfig = Properties().apply {
    rootProject.file("app-config.properties").inputStream().use { load(it) }
}
android {
    namespace = "br.com.universalbuild.core.network"
    compileSdk = appConfig.getProperty("compileSdk").toInt()
    defaultConfig {
        minSdk = appConfig.getProperty("minSdk").toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
kotlin { jvmToolchain(17) }
dependencies {
    api(libs.retrofit)
    api(libs.okhttp)
    implementation(libs.okhttp.logging)
}

detekt {
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}
