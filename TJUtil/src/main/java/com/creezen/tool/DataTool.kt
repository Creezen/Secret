package com.creezen.tool

import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import android.view.MotionEvent
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import kotlin.math.pow
import kotlin.math.sqrt

object DataTool {

    fun init() {}

    fun calculateMultiPointDistance(event: MotionEvent): Float {
        return calculateDistance(
            event.getX(0),
            event.getY(0),
            event.getX(1),
            event.getY(1))
    }

    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val distX = (x1 - x2).toDouble().pow(2.0).toFloat()
        val distY = (y1 - y2).toDouble().pow(2.0).toFloat()
        return sqrt(distX + distY)
    }

    inline fun <reified T> loadDataFromYAML(path: String): T? {
        kotlin.runCatching {
            val yaml = Yaml(Constructor(T::class.java, LoaderOptions()))
            val source = BaseTool.env().resources.assets.open("yaml/$path.yaml")
            val values = yaml.load<T>(source)
            source.close()
            return values
        }
        return null
    }

    fun Float.dpToPx(resources: Resources): Float {
        return this * resources.displayMetrics.density
    }

    fun Float.spToPx(resources: Resources): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, resources.displayMetrics)
    }

    fun Float.pxToDp(resources: Resources): Float {
        return this / resources.displayMetrics.density
    }

    fun Float.pxToSp(resources: Resources): Float {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            TypedValue.deriveDimension(TypedValue.COMPLEX_UNIT_SP, this, resources.displayMetrics)
        } else {
            this / resources.displayMetrics.scaledDensity
        }
    }

    fun getNumberList(num: Int, offset: Int = 0): Array<String> {
        return ArrayList<String>().apply {
            repeat(num) {
                add((it + offset).toString())
            }
        }.toTypedArray()
    }
}