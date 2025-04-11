package com.jayce.vexis.creezenlint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression


class CreezenLint : Detector(), Detector.UastScanner {

    companion object {
        val issue = Issue.create(
            "ljwtodo",
            "",
            "",
            category = Category.LINT,
            priority = 9,
            severity = Severity.WARNING,
            implementation = Implementation(
                CreezenLint::class.java,
                Scope.JAVA_AND_RESOURCE_FILES
            )
        )
    }

    override fun getApplicableMethodNames(): List<String> {
        return listOf("TODO")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if(context.evaluator.isMemberInClass(method, "kotlin.StandardKt__StandardKt")) {
            val fix = fix().name("ddd").replace().all().build()
            context.report(issue, context.getLocation(node), "sdf", fix)
        }
        context.client.log(Severity.ERROR, null ,"hello this is lint")
    }
}