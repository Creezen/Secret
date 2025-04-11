package com.jayce.vexis.creezenlint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

class MyRegistry : IssueRegistry() {

    init {
        println(">>> MyRegistry is loaded!")
    }

    override val minApi: Int
        get() = 14

    override val api: Int
        get() = 14

    override val vendor: Vendor
        get() = Vendor(
            "ddd",
            "sddsffgdfg"
        )
    override val issues: List<Issue>
        get() = listOf(CreezenLint.issue)

}