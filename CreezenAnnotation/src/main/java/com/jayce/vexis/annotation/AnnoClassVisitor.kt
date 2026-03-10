package com.jayce.vexis.annotation

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AnnoClassVisitor(
    visitor: ClassVisitor,
    val name: String
) : ClassVisitor(Opcodes.ASM9, visitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == null || descriptor == null) return mv
        return AnnoMethodVisitor(api, mv, access, name, descriptor)
    }

}