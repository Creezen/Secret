package com.jayce.vexis.foundation.ui.block.image

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.creezen.tool.AndroidTool.registerGestureEvent
import com.creezen.tool.ability.click.GestureCallback
import com.creezen.tool.ability.click.Point
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class ScaleImage(mContext: Context, private var isDrag: Boolean = false, attr: AttributeSet? = null) : AppCompatImageView(mContext, attr), GestureCallback {

    constructor(mContext: Context, attr: AttributeSet? = null) : this(mContext, false, attr)

    private var matrix = Matrix()
    private lateinit var fullFlag: AtomicBoolean
    private var previewFlag: Boolean = false
    private val dialog by lazy { ImageDialog(mContext) }

    init {
        scaleType = if (isDrag) ScaleType.MATRIX else ScaleType.FIT_CENTER
        if (isDrag) fullFlag = AtomicBoolean(true)
        adjustViewBounds = !isDrag
        registerGestureEvent("image", this)
    }

    fun setDraggable(draggable: Boolean) {
        isDrag = draggable
        scaleType = if (isDrag) ScaleType.MATRIX else ScaleType.FIT_CENTER
        adjustViewBounds = !isDrag
    }

    fun setPreview(preview: Boolean) {
        previewFlag = preview
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!::fullFlag.isInitialized) return
        if (!fullFlag.get()) return
        if (!isDrag) return

        var rect = getRealWidth()
        val scaleX = measuredWidth.toFloat() / rect.width()
        val scaleY = measuredHeight.toFloat() / rect.height()
        val scale = min(scaleX, scaleY)
        matrix.postScale(scale, scale)
        imageMatrix = matrix

        rect = getRealWidth()
        val paddingX = (measuredWidth - rect.width()) * 0.5f
        val paddingY = (measuredHeight - rect.height()) * 0.5f
        matrix.postTranslate(paddingX, paddingY)
        imageMatrix = matrix

        fullFlag.set(false)
    }

    private fun getRealWidth(): RectF {
        val imageWidth = drawable.intrinsicWidth
        val imageHeight = drawable.intrinsicHeight
        val srcRect = RectF(0f, 0f, imageWidth.toFloat(), imageHeight.toFloat())
        val dstRect = RectF()
        imageMatrix.mapRect(dstRect, srcRect)
        return dstRect
    }

    override fun onZoomIn(viewId: String, scaleFactor: Float, point: Point): Boolean {
        if (!isDrag) return false
        matrix.postScale(scaleFactor, scaleFactor, point.x, point.y)
        imageMatrix = matrix
        return true
    }

    override fun onZoomOut(viewId: String, scaleFactor: Float, point: Point): Boolean {
        if (!isDrag) return false
        matrix.postScale(scaleFactor, scaleFactor, point.x, point.y)
        imageMatrix = matrix
        return false
    }

    override fun onMove(viewId: String, points: Int, dx: Float, dy: Float): Boolean {
        if (!isDrag) return false
        matrix.postTranslate(dx, dy)
        imageMatrix = matrix
        return true
    }

    override fun onDoubleClick(viewId: String): Boolean {
        if (!previewFlag) return false
        dialog.show(drawable)
        return super.onDoubleClick(viewId)
    }

    fun setFullScreen() {
        fullFlag = AtomicBoolean(true)
        invalidate()
    }
}