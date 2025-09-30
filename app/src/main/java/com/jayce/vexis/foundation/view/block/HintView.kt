package com.jayce.vexis.foundation.view.block

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.WindowInsetsCompat
import com.creezen.tool.DataTool.dpToPx
import com.creezen.tool.WindowTool.requestFloatWindow
import com.jayce.vexis.R
import com.jayce.vexis.databinding.HintTextFloatWindowBinding

class HintView(context: Context, attr: AttributeSet) : AppCompatEditText(context, attr) {

    private var iconRight: Int = 0
    private var iconLeft: Int = 0
    private var iconBottom: Int = 0
    private var iconTop: Int = 0

    private var showIcon: Boolean = false
    private var iconText: String = ""
    private var iconMargin: Float = 0f

    private val iconDrawable by lazy {
        AppCompatResources.getDrawable(context, R.drawable.wrong)
    }

    init {
        context.obtainStyledAttributes(attr, R.styleable.HintView).use {
            showIcon = it.getBoolean(R.styleable.HintView_showIcon, false)
            iconText = it.getString(R.styleable.HintView_iconText) ?: ""
            iconMargin = it.getDimension(R.styleable.HintView_iconMargin, 0f)
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (!showIcon) return
        val icon = iconDrawable ?: return
        iconRight = width - paddingEnd - iconMargin.toInt()
        iconLeft = iconRight - textSize.toInt()
        iconBottom = height - paddingBottom
        iconTop = iconBottom - textSize.toInt()
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return true
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (isIconClick(event.x, event.y)) {
                    val statusBarHeight = rootWindowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                    val textView = HintTextFloatWindowBinding.inflate(LayoutInflater.from(context)).root
                    textView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
                    val intArray = IntArray(2)
                    getLocationOnScreen(intArray)
                    requestFloatWindow(context, textView, 2000) {
                        x = intArray[0] + iconLeft - textView.measuredWidth + (textSize / 2).toInt()
                        y = intArray[1] + iconBottom - statusBarHeight
                        this
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun setIconVisible(isShow: Boolean) {
        showIcon = isShow
        invalidate()
    }

    fun setIconHintText(hintText: String) {
        iconText = hintText
    }

    private fun isIconClick(x: Float, y: Float): Boolean {
        return Rect(iconLeft, iconTop, iconRight,iconBottom).contains(x.toInt(), y.toInt())
    }
}