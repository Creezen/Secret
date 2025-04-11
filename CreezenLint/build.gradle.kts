plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
    id("kotlin")
}

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
    manifest {
        attributes (
            "Lint-Registry-v2" to "com.jayce.vexis.creezenlint.MyRegistry"
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenB"){
            groupId = "com.creezen.tool.lint"
            artifactId = "lintTool"
            version = "1.0.0"
            from(components["java"])
            tasks.matching { it.name == "generateMetadataFileForMavenBPublication" }.configureEach{
                dependsOn(tasks.jar)
            }
        }
    }
    repositories {
        mavenLocal()
    }
}

dependencies {
    val version = "31.0.2"
    compileOnly("com.android.tools.lint:lint-api:$version")
    compileOnly("com.android.tools.lint:lint-checks:$version")
}
