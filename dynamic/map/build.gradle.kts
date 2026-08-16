import com.android.build.api.variant.impl.VariantOutputImpl

fun buildDir(): String {
    return layout.buildDirectory.get().asFile.path
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

android {
    namespace = "com.jayce.vexis.map"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jayce.vexis.map"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        ndk {
            abiFilters.add("arm64-v8a")
        }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    aaptOptions {
        additionalParameters.add("--package-id")
        additionalParameters.add("0x7E")
        additionalParameters.add("--allow-reserved-package-id")
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        jniLibs {
            pickFirsts.add("lib/arm64-v8a/libc++_shared.so")
            pickFirsts.add("lib/armeabi-v7a/libc++_shared.so")
        }
    }
    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
        showAll = true
        htmlOutput = file("${buildDir()}/reports/combined/lint-report.html")
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.filterIsInstance<VariantOutputImpl>().forEach { output ->
            output.outputFileName = "${variant.name}-map.apk"
        }
    }
}

val deployPath = File("D:/FileSystem/APK/map")
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
    implementation(libs.tools)
    implementation(libs.amap.navigation)
}