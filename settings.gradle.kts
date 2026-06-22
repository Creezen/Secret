pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = java.net.URI("https://jitpack.io")
        }
    }
//    includeBuild("CreezenAnnotation")
}

rootProject.name = "Secret"
include(":app")
include(":ClientUtil")
include(":dynamicLib")
include(":CreezenAnnotation")
