package com.jayce.vexis.foundation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.CheckBox
import android.widget.LinearLayout
import com.jayce.vexis.R
import kotlin.math.ceil

import com.jayce.vexis.databinding.TextCheckBoxBinding

class MultiLineSingleCheckbox(
    private val context: Context,
    attr: AttributeSet,
) : LinearLayout(context, attr) {
    private var itemOneLine: Int = 1
    private var childItemCount = 0
    private val strList = arrayListOf<String>()
    private var previewSelected = -1
    private val childList = arrayListOf<CheckBox>()
    private var isInit = false

    init {
        val styleAttribute = context.obtainStyledAttributes(attr, R.styleable.MultiLineSingleCheckbox)
        itemOneLine = styleAttribute.getInt(R.styleable.MultiLineSingleCheckbox_itemOneLine, 1)
        styleAttribute.recycle()
    }

    fun selectedItem(): String {
        return if (previewSelected < 0) {
            ""
        } else {
            strList[previewSelected]
        }
    }

    fun setChildLayout(
        list: ArrayList<String>,
        backgroundId: Int = R.drawable.checkbox_style,
        onItemSelect: (String) -> Unit
    ) {
        if (isInit) return
        isInit = true
        strList.addAll(list)
        childItemCount = strList.size
        orientation = VERTICAL
        removeAllViews()
        val result = ceil(childItemCount.toDouble() / itemOneLine).toInt()
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        for (i in 0 until result) {
            addLayout(i, backgroundId, onItemSelect)
        }
    }

    private fun addLayout(
        position: Int,
        backgroundId: Int?,
        onItemSelect: (String) -> Unit,
    ) {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = HORIZONTAL
        linearLayout.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        for (i in position * itemOneLine until (position + 1) * itemOneLine) {
            if (i >= childItemCount) {
                break
            }
            val child = TextCheckBoxBinding.inflate(LayoutInflater.from(context)).root
            child.text = strList[i]
            backgroundId?.let {
                child.setBackgroundResource(it)
            }
            child.layoutParams = LayoutParams(0, MATCH_PARENT, 1f)
            child.setOnClickListener {
                setClickAction(i)
                val str = if(previewSelected < 0) {
                    "请输入你的建议..."
                } else {
                    "请描述你认为${strList[i]}的理由..."
                }
                onItemSelect.invoke(str)
            }
            childList.add(child)
            linearLayout.addView(child)
        }
        addView(linearLayout)
    }

    private fun setClickAction(i: Int) {
        if (previewSelected < 0) {
            childList[i].isChecked = true
            previewSelected = i
            return
        }
        if (i == previewSelected) {
            childList[i].isChecked = false
            previewSelected = -1
        } else {
            childList[previewSelected].isChecked = false
            childList[i].isChecked = true
            previewSelected = i
        }
    }
}
