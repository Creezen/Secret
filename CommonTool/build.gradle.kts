import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-library")
    id("maven-publish")
}

group = "com.creezen.tool.commontool"
version = "1.0.0"

repositories {
    mavenLocal()
    google()
    mavenCentral()
    gradlePluginPortal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        create<MavenPublication>("mavenB"){
            groupId = "com.creezen.tool.commontool"
            artifactId = "tools"
            version = "1.0.0"
            from(components["kotlin"])
            tasks.matching { it.name == "generateMetadataFileForMavenBPublication" }.configureEach{
                dependsOn(tasks.named<Jar>("sourceJar"))
            }
        }
    }
    repositories {
        mavenLocal()
    }
}

dependencies {
    implementation (libs.fastjson)
    implementation(libs.gson)
}