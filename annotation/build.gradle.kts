plugins {
    id("java-gradle-plugin")
    id("java-library")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compileOnly("com.android.tools.build:gradle:9.1.0")
    implementation(gradleApi())
    implementation("org.ow2.asm:asm:9.8")
    implementation("org.ow2.asm:asm-commons:9.8")
}

gradlePlugin {
    plugins {
        create("annotation") {
            id = "com.jayce.vexis.annotation"
            implementationClass = "com.jayce.vexis.annotation.AnnotationPlugin"
        }
    }
}
