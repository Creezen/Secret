package com.jayce.vexis.foundation.ui.block.time

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.creezen.tool.ThreadTool
import com.jayce.vexis.R
import com.jayce.vexis.domain.bean.TimeUnitEntry
import com.jayce.vexis.domain.enums.TimePickerType

class TimePicker(context: Context, attr: AttributeSet) : LinearLayout(context, attr) {

    private var timeStyle: Int = 0
    private var timePicker: ITimePicker<out ViewBinding>
    private val timePickerFactory: TimePickerFactory = TimePickerFactory()

    init {
        context.obtainStyledAttributes(attr, R.styleable.TimePicker).use {
            timeStyle = it.getInt(R.styleable.TimePicker_timeStyle, 0)
        }
        orientation = HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        timePicker = timePickerFactory.getTimePicker(TimePickerType.getType(timeStyle))
        timePicker.initLayout(context, this)
        ThreadTool.runOnMain { timePicker.initUI() }
    }

    fun time(): TimeUnitEntry {
        return timePicker.getTime()
    }

    fun formatTime(normal: Boolean = false): String {
        val length = if (timeStyle == 0) 3 else 7
        return if (normal) {
            time().formatString(length)
        } else {
            time().formatString('0', length)
        }
    }

    fun setOnTimePickerChange(onTimeChange: TimeUnitEntry.() -> Unit) {
        timePicker.setOnTimePickerChange(onTimeChange)
    }

    fun setTime(time: String) = timePicker.setTime(time)
}