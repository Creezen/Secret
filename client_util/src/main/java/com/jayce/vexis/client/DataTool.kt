package com.jayce.vexis.client

import android.os.Build
import android.util.TypedValue
import android.view.MotionEvent
import com.jayce.vexis.client.BaseTool.envContext
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import kotlin.math.max
import kotlin.math.min
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
            val source = BaseTool.envContext.resources.assets.open("yaml/$path.yaml")
            val values = yaml.load<T>(source)
            source.close()
            return values
        }
        return null
    }

    fun Float.dpToPx(): Float {
        val resources = envContext.resources
        return this * resources.displayMetrics.density
    }

    fun Float.spToPx(): Float {
        val resources = envContext.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, resources.displayMetrics)
    }

    fun Float.pxToDp(): Float {
        val resources = envContext.resources
        return this / resources.displayMetrics.density
    }

    fun Float.pxToSp(): Float {
        val resources = envContext.resources
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

    fun editScore(start: String, dest: String): Float {
        val distance = editDistance(start, dest)
        val length = max(start.length, dest.length)
        return 1 - distance.toFloat() / length.toFloat()
    }

    fun editDistance(start: String, dest: String): Int {
        val m = start.length
        val n = dest.length
        val matrix = Array(m + 1) { IntArray(n + 1) }
        for (i in 0 .. m) matrix[i][0] = i
        for (i in 0 .. n) matrix[0][i] = i
        for (i in 1 .. m) {
            for (j in 1 .. n) {
                val cm = start[i - 1]
                val cn = dest[j - 1]
                val modify = if (cm == cn) 0 else 1
                matrix[i][j] = min(min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1), matrix[i - 1][j - 1] + modify)
            }
        }
        return matrix[m][n]
    }
}