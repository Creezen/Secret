import com.android.build.api.variant.impl.VariantOutputImpl

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
    namespace = "com.jayce.vexis.dynamic"
    compileSdk = 35

    defaultConfig {
        minSdk = 30
        versionCode = 1000000
        versionName = "1.0.0"
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
}

androidComponents {
    onVariants { variant ->
        variant.outputs.filterIsInstance<VariantOutputImpl>().forEach { output ->
            output.outputFileName = "${variant.name}-1.apk"
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.tools)
    implementation(libs.common.tools)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}