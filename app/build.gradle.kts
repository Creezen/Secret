import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("maven-publish")
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
}

apply("${rootProject.projectDir}/config.gradle")

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

android {
    namespace = "com.jayce.vexis"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.jayce.vexis"
        minSdk = 30
        targetSdk = 34
        versionCode = 1000000
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "baseUrl", "${project.ext["localUrl"]}")
            buildConfigField("String", "socketUrl", "${project.ext["localSocketPath"]}")
            buildConfigField("Integer", "socketPort", "${project.ext["localSocketPort"]}")
        }

        debug {
            buildConfigField("String", "baseUrl", "${project.ext["localUrl"]}")
            buildConfigField("String", "socketUrl", "${project.ext["localSocketPath"]}")
            buildConfigField("Integer", "socketPort", "${project.ext["localSocketPort"]}")
        }

        create("cloud") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "baseUrl", "${project.ext["cloudUrl"]}")
            buildConfigField("String", "socketUrl", "${project.ext["cloudSocketPath"]}")
            buildConfigField("Integer", "socketPort", "${project.ext["cloudSocketPort"]}")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    lint {
        enable.add("HardcodedText")
        baseline = file("lint-baseline.xml")
        abortOnError = false
        htmlOutput = file("$buildDir/reports/combined/lint-report.html")
    }
}

ktlint {
    ignoreFailures.set(true)
    android.set(true)
    reporters {
        reporter(ReporterType.HTML)
    }
}

tasks.register<DefaultTask>("kt-lint") {
    group = "verification"
    dependsOn("ktlintCheck")
    doLast {
        val sourceDir = file("$buildDir/reports/ktlint")
        val outputFile = file("$buildDir/reports/combined/kt-report.html")

        if (!outputFile.parentFile.exists()) {
            outputFile.parentFile.mkdirs() // 递归创建目录
        }

        // 创建合并后的基础文档
        val mergedDoc: Document = Document.createShell("")
        mergedDoc.title("Merged Ktlint Report")
        mergedDoc.head().append(
            """
            <link href="https://fonts.googleapis.com/css?family=Source+Code+Pro" rel="stylesheet">
            <style>
                body { font-family: 'Source Code Pro', monospace; }
                h3 { font-size: 12pt; }
            </style>
            """.trimIndent(),
        )

        val body =
            mergedDoc.body().apply {
                appendElement("h1")
                    .text("Merged Ktlint Report")
            }

        var totalIssues = 0
        val htmlFiles =
            sourceDir.listFiles { file ->
                val subFile = file?.listFiles()?.get(0) ?: return@listFiles false
                subFile.isFile && subFile.extension == "html"
            }?.sortedBy { it.name }
        if (htmlFiles.isNullOrEmpty()) return@doLast
        htmlFiles.forEach { fileParent ->
            val path = "${fileParent.absolutePath}/${fileParent.name}.html"
            val doc = Jsoup.parse(File(path), "UTF-8")
            // 提取 Issues found 数值
            val issuesFoundPara = doc.select("body > p:contains(Issues found)").first()
            val issuesCount =
                issuesFoundPara?.text()?.let { text ->
                    Regex("\\d+").find(text)?.value?.toInt() ?: 0
                } ?: 0
            totalIssues += issuesCount

            // 提取文件名和问题列表（修复选择器）
            doc.select("body > h3, body > ul").forEach { element ->
                val clonedElement = element.clone()
                if (clonedElement.tagName() == "h3") {
                    // 添加分隔线提升可读性
                    body.appendElement("hr")
                    body.appendChild(clonedElement)
                } else {
                    body.appendChild(clonedElement)
                }
            }
        }

        body.prependChild(
            Element("p").text("Total Issues found: $totalIssues"),
        )

        val mergedContent = body.outerHtml()
        if (mergedContent.contains("<h3>")) {
            outputFile.writeText(mergedContent)
        }
    }
}

tasks.register("mergeReports") {
    group = "verification"

    dependsOn("lint", "kt-lint").doLast {
        delete("$buildDir/reports/ktlint")
        val file = fileTree("$buildDir/reports/")
        file.forEach {
            if (it.name.contains("lint-result")) {
                delete(it)
            }
        }
    }

    doLast {
        val outputDir = file("$buildDir/reports/combined")
        outputDir.mkdirs()
        val mergedReport = File(outputDir, "merged-report.html")

        val ktlintDoc = Jsoup.parse(file("$buildDir/reports/combined/kt-report.html").readText())
        val lintDoc = Jsoup.parse(file("$buildDir/reports/combined/lint-report.html").readText())

        lintDoc.body()
            .select("main.mdl-layout__content div.mdl-layout__tab-panel.is-active")
            .first()
            ?.append(ktlintDoc.body().html())

        // 保存合并后的文件
        mergedReport.writeText(lintDoc.html())
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.room:room-runtime:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.alibaba:fastjson:2.0.40")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.github.stuxuhai:jpinyin:1.1.7")
    implementation("q.rorbin:badgeview:1.1.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    api("com.creezen.tool:tools:1.0.0")
}