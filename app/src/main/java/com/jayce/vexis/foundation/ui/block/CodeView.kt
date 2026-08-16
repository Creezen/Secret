package com.jayce.vexis.foundation.ui.block

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.jayce.vexis.client.BaseTool.envContext
import com.jayce.vexis.client.FileTool
import com.jayce.vexis.client.FileTool.getDir
import com.jayce.vexis.client.TLog
import com.jayce.vexis.databinding.CodeLayoutBinding
import java.io.File

@SuppressLint("SetJavaScriptEnabled")
class CodeView(context: Context, attr: AttributeSet) : FrameLayout(context, attr) {

    private val bind by lazy {
        CodeLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private var isReady: Boolean = false
    private var isRendered: Boolean = false
    private var pendingCode: String = ""
    private var pendingLang: String = ""
    private var pendingTheme: String = ""

    init {
        bind.web.apply {
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            addJavascriptInterface(JsInterface(), "Android")

            val baseFile = getDir(FileTool.Dir.LOC_PRIVATE_FILE, envContext)
            val shikiFile = File(baseFile, "shiki/shikiweb.html")
            loadUrl("file://${shikiFile.absolutePath}")
        }
    }

    /**
     * 执行js中返回的过来的回调，
     * 以下方法，对应于html里面scrip部分的window.Android.onReady()
     */
    inner class JsInterface {
        @JavascriptInterface
        fun onReady() {
            isReady = true
            renderCode(pendingCode, pendingLang, pendingTheme)
        }
    }

    fun code(code: String, lang: String = "kotlin", theme: String = "github-dark") {
        isRendered = false
        if (isReady) {
            renderCode(code, lang, theme)
        } else {
            pendingCode = code
            pendingLang = lang
            pendingTheme = theme
        }
    }

    @Synchronized
    private fun renderCode(code: String, lang: String, theme: String) {
        if (isRendered) return
        val escapedCode = code
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")

        val script = """
            window.renderCode("$escapedCode", "$lang", "$theme");
        """.trimIndent()

        post { bind.web.evaluateJavascript(script) { /**/ } }
        isRendered = true
    }
}