plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("maven-publish")
}

repositories {
    google()
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
    maven {
        url = uri("https://zrzklsaaov5s.xiaomiqiu.com/repository/")
    }
}

android {
    namespace = "com.jayce.vexis.util.client"
    compileSdk = 35

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags("")
            }
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

    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "4.1.2"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenA"){
            groupId = "com.jayce.vexis.util"
            artifactId = "client"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    ksp(libs.room.compile)
    implementation(libs.core.ktx)
    implementation(platform(libs.kotlin.bom))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.lifecycle.common.jvm)
    implementation (libs.room.runtime)
    implementation (libs.retrofit)
    implementation (libs.retrofit.converter.gson)
    implementation (libs.retrofit.converter.scalars)
    implementation (libs.glide)
    implementation(libs.kotlin.stdlib)
    implementation(libs.mmkv)
    implementation(libs.snakeyaml)
    implementation(libs.glide.okhttp3.integration)
    implementation(libs.datastore)
    implementation(libs.pytorch.android)
    implementation(libs.pytorch.android.torchvision)
    implementation(libs.common.tool)
}