plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dokka)
    id("maven-publish")
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
    gradlePluginPortal()
}

android {
    namespace = "com.example.testlib"
    compileSdk = 35

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "17"
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

val processedSourcesDir = buildDir.resolve("processed-sources")
// 自定义任务：处理 Kotlin 文件
val processSources by tasks.register<Copy>("processSources") {
    doFirst {
        processedSourcesDir.mkdirs()
    }
    from(android.sourceSets["main"].java.srcDirs) {
        // 处理 Kotlin 文件
        eachFile {
            if (file.name.endsWith(".kt")) {
                // 获取文件的相对路径
                val relativePath = relativePath(file.parentFile)
                // 构建目标文件路径
                val outputFile = processedSourcesDir.resolve(relativePath).resolve(file.name)
                outputFile.parentFile.mkdirs()
                outputFile.writeText(processKotlinFile(file))
                exclude()
            }
        }
    }
    into(processedSourcesDir)
    // 保留文件结构
    includeEmptyDirs = false
}

// 处理 Kotlin 文件，移除方法体
fun processKotlinFile(file: File): String {
    val content = file.readText()
    return content
}

val sourcesJar by tasks.register<Jar>("sourcesJar") {
    dependsOn(tasks.named("generateMetadataFileForMavenAPublication"))
    dependsOn(processSources)
    archiveClassifier.set("sources")
    from(processedSourcesDir)
}

publishing {
    publications {
        create<MavenPublication>("mavenA"){
            groupId = "com.creezen.tool"
            artifactId = "tools"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
//            artifact(sourcesJar)
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
    implementation(libs.gson)
    implementation(libs.mmkv)
    implementation(libs.snakeyaml)
    implementation(libs.glide.okhttp3.integration)
    implementation(libs.common.tools)
}