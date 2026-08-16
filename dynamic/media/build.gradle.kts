
fun buildDir(): String {
    return layout.buildDirectory.get().asFile.path
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.jayce.vexis.media"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jayce.vexis.media"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    aaptOptions {
        additionalParameters.add("--package-id")
        additionalParameters.add("0x7E")
        additionalParameters.add("--allow-reserved-package-id")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
        showAll = true
        htmlOutput = file("${buildDir()}/reports/combined/lint-report.html")
    }
}

val deployPath = File("D:/FileSystem/APK/media")
tasks.register<Copy>("deploy") {
    group = "deploy"
    dependsOn("assembleDebug")
    from("build/outputs/apk/debug") { include("*.apk") }
    into(deployPath)
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.media.player)
    implementation(libs.media.ui)
    implementation(libs.media.sesison)
    implementation(libs.tools)
}