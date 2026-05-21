package com.jayce.vexis.foundation.ui.block

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.creezen.tool.DataTool.dpToPx
import com.jayce.vexis.R
import com.jayce.vexis.databinding.LevelTextviewBinding

class LevelTextView(context: Context, attr: AttributeSet) : LinearLayout(context, attr) {

    private val binding: LevelTextviewBinding
    var level: Int = 0
        set(value) {
            binding.level.text = "LV$value"
            field = value
            setColor(value)
        }

    var isAdmin: Boolean = false
        set(value) {
            val visibility = if (value) VISIBLE else GONE
            binding.admin.visibility = visibility
            field = value
        }

    var size: Float = 20f
        set(value) {
            binding.name.textSize = value
            binding.level.textSize = value * 0.3f
            binding.admin.textSize = value * 0.3f
            field = value
        }

    var userName: String = "未知用户"
        set(value) {
            binding.name.text = value
            field = value
        }

    init {
        orientation = HORIZONTAL
        binding = LevelTextviewBinding.inflate(LayoutInflater.from(context), this)
        context.obtainStyledAttributes(attr, R.styleable.LevelTextView).use {
            level = it.getInt(R.styleable.LevelTextView_level, 0)
            isAdmin = it.getBoolean(R.styleable.LevelTextView_isAdmin, false)
            size = it.getFloat(R.styleable.LevelTextView_size, 20f)
            userName = it.getString(R.styleable.LevelTextView_userName) ?: "未知用户"
        }
    }

    private fun setColor(level: Int) {
        binding.level.apply {
            val color = getColorResource(level)
            setTextColor(color)
            val drawable = background.mutate() as? GradientDrawable
            drawable?.setStroke(1.0f.dpToPx().toInt(), color)
        }
    }

    private fun getColorResource(level: Int): Int {
        val resource = context.resources.getIntArray(R.array.levelColorArray)
        return resource[level]
    }
}