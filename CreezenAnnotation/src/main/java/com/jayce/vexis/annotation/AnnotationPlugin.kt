package com.jayce.vexis.annotation

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AnnotationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val component = project.extensions.findByType(AndroidComponentsExtension::class.java)
        component?.onVariants {
            it.instrumentation.transformClassesWith(
                AnnoFactory::class.java,
                InstrumentationScope.ALL
            ) {}
            it.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
        }
    }
}