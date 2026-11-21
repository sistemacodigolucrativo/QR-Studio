import java.util.Properties
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}
val appConfig = Properties().apply {
    rootProject.file("app-config.properties").inputStream().use { load(it) }
}
android {
    namespace = "br.com.universalbuild.core.ui"
    compileSdk = appConfig.getProperty("compileSdk").toInt()
    defaultConfig {
        minSdk = appConfig.getProperty("minSdk").toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
kotlin { jvmToolchain(17) }
dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
}

detekt {
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}
