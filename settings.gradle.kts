pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = java.net.URI("https://jitpack.io")
        }
    }
    includeBuild("annotation")
}

rootProject.name = "Secret"
include(":app")
include(":client_util")
include(":dynamic")
include(":issue")
