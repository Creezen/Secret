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
        classpath("org.jsoup:jsoup:1.16.1")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt) apply false
}