package com.creezen.tool

import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import com.creezen.tool.DataTool.dpToPx
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Construct
import org.yaml.snakeyaml.constructor.Constructor
import java.lang.reflect.Type
import kotlin.math.pow
import kotlin.math.sqrt

object DataTool {

    const val TAG = "DataTool"

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

    @Deprecated("use toData instead!")
    inline fun <reified T> jsonToData(jsonStr: String): T? {
        kotlin.runCatching {
            val type = object : TypeToken<T>() {}.type
            return Gson().fromJson(jsonStr, type) as T
        }.onFailure {
            Log.e(TAG, "jsonToData error: $it")
            it.printStackTrace()
        }
        return null
    }

    private fun dataToJson(any: Any): String? {
        kotlin.runCatching {
            return Gson().toJson(any)
        }.onFailure {
            Log.e(TAG,"dataToJson error: $it")
        }
        return null
    }

    fun Any.toJson() = dataToJson(this)

    inline fun <reified T> String.toData() = jsonToData<T>(this)

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
}