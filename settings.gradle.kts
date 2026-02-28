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

rootProject.name = "YandexMusic"

include(":app")
include(":core:network")
include(":core:data")
include(":core:ui")
include(":feature:home")
include(":feature:search")
include(":feature:library")
include(":feature:auth")
