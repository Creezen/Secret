import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    id("kotlin-parcelize")
    id("maven-publish")
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
}

apply("${rootProject.projectDir}/config.gradle")

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

detekt {
    ignoreFailures = true
}

android {
    namespace = "com.jayce.vexis"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.jayce.vexis"
        minSdk = 30
        targetSdk = 35
        versionCode = 1000000
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }
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

    dependsOn("detekt")

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

    api(libs.tools)
    ksp(libs.room.compile)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.room.runtime)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.viewpager2)
    implementation(libs.glide)
    implementation(libs.jpinyin)
    implementation(libs.badgeview)
    implementation(libs.zxing.android.embedded)
    implementation(libs.preference.ktx)
    implementation(libs.koin.android)
    implementation(libs.common.tools)
    implementation(libs.amap)
}