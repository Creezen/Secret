package com.jayce.vexis.issue

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.Issue

class XmlIssueRegistry : IssueRegistry() {

    override val vendor: Vendor = Vendor("creezen")

    override val issues: List<Issue>
        get() = listOf(HardcodeResourceDetector.ISSUE)
}