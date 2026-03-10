package com.jayce.vexis.annotation

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

abstract class AnnoFactory : AsmClassVisitorFactory<InstrumentationParameters> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val name = classContext.currentClassData.className
        return AnnoClassVisitor(nextClassVisitor, name)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className
        return className.startsWith("com.jayce.vexis")
    }

}