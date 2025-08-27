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
    id("com.android.application") version "8.9.0-alpha05" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("com.android.library") version "8.9.0-alpha05" apply false
    id("org.jetbrains.kotlin.jvm") version "2.1.0" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
}