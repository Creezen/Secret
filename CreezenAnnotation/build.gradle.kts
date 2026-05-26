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

    compileOnly("com.android.tools.lint:lint:31.10.1")
    compileOnly("com.android.tools.lint:lint-api:31.10.1")
    compileOnly("com.android.tools.lint:lint-checks:31.10.1")
}

gradlePlugin {
    plugins {
        create("creezenAnnotation") {
            id = "com.jayce.vexis.annotation"
            implementationClass = "com.jayce.vexis.annotation.AnnotationPlugin"
        }
    }
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes(
            "Lint-Registry-v2" to "com.jayce.vexis.issue.XmlIssueRegistry"
        )
    }
}
