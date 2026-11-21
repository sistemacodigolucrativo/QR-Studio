import java.util.Properties
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}
val appConfig = Properties().apply {
    rootProject.file("app-config.properties").inputStream().use { load(it) }
}
android {
    namespace = "br.com.universalbuild.core.database"
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
    api(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}

// Versioned Room schema is exported for review during database evolution.
ksp { arg("room.schemaLocation", file("schemas").absolutePath) }

detekt {
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}
