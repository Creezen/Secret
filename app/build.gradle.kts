plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("maven-publish")
}

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

android {
    namespace = "com.jayce.vexis"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jayce.vexis"
        minSdk = 30
        targetSdk = 34
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
            signingConfig = signingConfigs.getByName("debug")
        }

        create("mock") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation ("androidx.room:room-runtime:2.5.2")
    kapt ("androidx.room:room-compiler:2.5.2")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")

    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    implementation ("com.alibaba:fastjson:2.0.40")
    implementation ("com.github.bumptech.glide:glide:4.15.1")

    implementation ("com.github.stuxuhai:jpinyin:1.1.7")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    api("com.creezen.tool:tools:1.0.0")
}