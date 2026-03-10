package com.jayce.vexis.annotation

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class AnnoMethodVisitor(
    api: Int,
    mv: MethodVisitor,
    access: Int,
    name: String,
    desc: String?
) : AdviceAdapter(api, mv, access, name, desc) {

    private var x: String? = null

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val va = super.visitAnnotation(descriptor, visible)
        println("descriptor: $descriptor")
        if (descriptor == "Lcom/jayce/vexis/foundation/ability/Logger;") {
            return object : AnnotationVisitor(api, va) {
                override fun visit(name: String?, value: Any?) {
                    if (name == "a") {
                        x = value as? String
                    }
                }
            }
        }
        return va
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        if (x.isNullOrEmpty()) return
        val msg = "a: $x"
        mv.visitLdcInsn("Logger")
        mv.visitLdcInsn(msg)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "android/util/Log",
            "d",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            false
        )
        mv.visitInsn(Opcodes.POP)
    }

}