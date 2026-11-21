pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "KotlinMasterBoilerplate"
include(":app")
include(":core:common", ":core:designsystem", ":core:network", ":core:database", ":core:ui")
include(":feature:home")
