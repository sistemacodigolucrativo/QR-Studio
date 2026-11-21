import java.util.Properties

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

val masterConfig = Properties().apply {
    rootProject.file("app-config.properties").inputStream().use { load(it) }
}
tasks.register("validateAppConfig") {
    group = "verification"
    description = "Validate the central app identity before generating Android variants."
    doLast {
        val id = masterConfig.getProperty("applicationId", "")
        val version = masterConfig.getProperty("versionName", "")
        val url = masterConfig.getProperty("baseApiUrl", "")
        require(id.matches(Regex("[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)+"))) { "Invalid applicationId" }
        require(version.matches(Regex("[0-9]+\\.[0-9]+\\.[0-9]+"))) { "versionName must use SemVer" }
        require(url.startsWith("https://")) { "baseApiUrl must use HTTPS" }
        require(masterConfig.getProperty("appName", "").isNotBlank()) { "Missing appName" }
        require(masterConfig.getProperty("brandColor", "").matches(Regex("#[0-9a-fA-F]{6}"))) { "brandColor must be #RRGGBB" }
        for (key in listOf("minSdk", "targetSdk", "compileSdk", "versionCode")) {
            require(masterConfig.getProperty(key, "").toIntOrNull()?.let { it > 0 } == true) { "Invalid " + key }
        }
    }
}
