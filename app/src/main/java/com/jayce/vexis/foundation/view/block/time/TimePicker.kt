package com.jayce.vexis.foundation.view.block.time

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.creezen.commontool.Config.Constant.EMPTY_STRING
import com.creezen.tool.ThreadTool
import com.jayce.vexis.R
import kotlinx.coroutines.Dispatchers

class TimePicker(context: Context, attr: AttributeSet): LinearLayout(context, attr) {

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
        ThreadTool.runOnMulti(Dispatchers.Main) {
            timePicker.initUI()
        }
    }

    fun time(): List<String> {
        return timePicker.getTime()
    }

    fun formatTime(): String {
        var timeStr = EMPTY_STRING
        time().forEachIndexed { i, v ->
            if(i == 0) {
                timeStr += v.padStart(4, '0')
            } else if(i == 6) {
                timeStr += v.padStart(3, '0')
            } else {
                timeStr += v.padStart(2, '0')
            }
        }
        return timeStr
    }
}