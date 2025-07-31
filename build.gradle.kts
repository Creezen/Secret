// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/releases")}
        maven { url = uri("https://maven.aliyun.com/repository/public")}
        maven { url = uri("https://maven.aliyun.com/repository/google")}
        maven { url = uri("https://maven.aliyun.com/repository/central")}
        maven { url = uri("https://maven.aliyun.com/repository/jcenter")}
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin")}
        maven { url = uri("https://maven.aliyun.com/repository/apache-snapshots")}
        maven { url = uri("https://maven.aliyun.com/repository/grails-core")}
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath("com.github.aasitnikov:fat-aar-android:b2a038763c")
        classpath("org.jsoup:jsoup:1.16.1")
    }
}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.0" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
}