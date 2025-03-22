plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("kotlin-kapt")
    id("com.kezong.fat-aar")
    id("org.jetbrains.dokka")
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
    gradlePluginPortal()
}

android {
    namespace = "com.example.testlib"
    compileSdk = 34

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

//    // 第一步：保护字符串内容（避免字符串中的大括号被误处理）
//    val protectedContent = content.replace(Regex("""(?s)(\"\"\"\".*?\"\"\"\"|"(?:\\.|[^"\\])*"|'(?:\\.|[^'\\])*')""")) { m ->
//        m.value.replace("{", "«").replace("}", "»")
//    }
//
//    // 第二步：移除方法体，但保留方法签名和注释
//    val result = protectedContent.replace(Regex("""(?s)\b(fun\s+((?!fun).)*?)(\s*\{[^{}]*?\}|\s*=.*?(?=\n|$))""")) {
//        it.groupValues[1].trimEnd()
//    }.replace(Regex("""(?s)\b(fun\s+((?!fun).)*?)(\s*\{.*?\.use\s*\{[^{}]*?\}.*?\}|\s*=.*?(?=\n|$))""")) {
//        it.groupValues[1].trimEnd()
//    }.replace(Regex("""(?s)\b(fun\s+((?!fun).)*?)(\s*\{.*?\.runCatching\s*\{[^{}]*?\}.*?\}|\s*=.*?(?=\n|$))""")) {
//        it.groupValues[1].trimEnd()
//    }.replace(Regex("""(?s)\b(fun\s+((?!fun).)*?)(\s*\{.*?if\s*\(.*?\)\s*\{[^{}]*?\}.*?\}|\s*=.*?(?=\n|$))""")) {
//        it.groupValues[1].trimEnd()
//    }.replace(Regex("""(?s)\b(fun\s+((?!fun).)*?)(\s*\{.*?when\s*\(.*?\)\s*\{[^{}]*?\}.*?\}|\s*=.*?(?=\n|$))""")) {
//        it.groupValues[1].trimEnd()
//    }.replace(Regex("""(?s)\b(fun\s+((?!fun).)*?)(\s*\{.*?\.let\s*\{[^{}]*?\}.*?\}|\s*=.*?(?=\n|$))""")) {
//        it.groupValues[1].trimEnd()
//    }
//
//    // 第三步：恢复字符串中的大括号
//    return result.replace(Regex("""«"""), "{").replace(Regex("""»"""), "}")
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
    offlineMode.set(true)
    dokkaSourceSets {
        configureEach {
            noAndroidSdkLink.set(true)
            includeNonPublic.set(true)
            reportUndocumented.set(true)
        }
    }
}

val dokkaHtmlJar by tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml.get().outputDirectory)
}

val sourcesJar by tasks.register<Jar>("sourcesJar") {
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
            artifact(dokkaHtmlJar)
            artifact(sourcesJar)
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.lifecycle:lifecycle-common-jvm:2.8.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    kapt ("androidx.room:room-compiler:2.5.2")
    implementation ("androidx.room:room-runtime:2.5.2")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")

    embed("com.creezen.tool.commontool:tools:1.0.0")
}