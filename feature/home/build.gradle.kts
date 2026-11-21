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
    namespace = "br.com.universalbuild.feature.home"
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
    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

detekt {
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}
