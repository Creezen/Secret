package com.jayce.vexis.issue

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.ResourceXmlDetector
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import org.w3c.dom.Attr

class HardcodeResourceDetector : ResourceXmlDetector() {

    companion object {
        val ISSUE = Issue.create(
            "detectResource",
            "资源硬编码",
            "资源硬编码",
            Category.CORRECTNESS,
            8,
            Severity.WARNING,
            Implementation(HardcodeResourceDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }

    private val prefixWhiteList = listOf("#")
    private val suffixWhiteList = listOf("px", "dp", "sp")
    private val otherWhiteList = listOf<String>()

    override fun getApplicableAttributes(): Collection<String>? {
        return ALL
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (shouldReport(attribute.name, attribute.value)) {
            context.report(
                ISSUE,
                attribute,
                context.getLocation(attribute),
                "Please Use resource instead."
            )
        }
    }

    private fun shouldReport(name: String, value: String): Boolean {
        prefixWhiteList.forEach {
            if (value.startsWith(it)) return true
        }
        suffixWhiteList.forEach {
            if (value.endsWith(it)) return true
        }
        otherWhiteList.forEach {
            if (value.contains(it)) return true
        }
        return false
    }
}