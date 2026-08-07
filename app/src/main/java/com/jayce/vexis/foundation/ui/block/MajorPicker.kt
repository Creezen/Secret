package com.jayce.vexis.foundation.ui.block

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import com.jayce.vexis.client.AndroidTool.init
import com.jayce.vexis.client.DataTool
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.databinding.MajorPickerLayoutBinding
import com.jayce.vexis.domain.bean.SubjectTableEntry

class MajorPicker(context: Context, attr: AttributeSet) : LinearLayout(context, attr) {

    private val binding: MajorPickerLayoutBinding
    private var disciplineList = listOf<String>()
    private var majorList = listOf<List<String>>()
    private var trackList = listOf<List<List<String>>>()

    init {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        binding = MajorPickerLayoutBinding.inflate(LayoutInflater.from(context), this)
    }

    fun init(discipline: String, major: String, track: String) {
        ThreadTool.runOnMulti {
            val subjectTableEntry = DataTool.loadDataFromYAML<SubjectTableEntry>("SubjectTable") ?: return@runOnMulti
            disciplineList = subjectTableEntry.discipline
            majorList = subjectTableEntry.category
            trackList = subjectTableEntry.major
            var disciplineIndex = disciplineList.indexOf(discipline)
            disciplineIndex = if (disciplineIndex < 0) 0 else disciplineIndex
            var majorIndex = majorList[disciplineIndex].indexOf(major)
            majorIndex = if (majorIndex < 0) 0 else majorIndex
            var trackIndex = trackList[disciplineIndex][majorIndex].indexOf(track)
            trackIndex = if (trackIndex < 0) 0 else trackIndex
            ui { initUI(disciplineIndex, majorIndex, trackIndex) }
        }
    }

    private fun initUI(disciplineIndex: Int, majorIndex: Int, trackIndex: Int) = binding.apply {
        discipline.init(disciplineList.toTypedArray(), disciplineIndex)
        major.init(majorList[disciplineIndex].toTypedArray(), majorIndex)
        track.init(trackList[disciplineIndex][majorIndex].toTypedArray(), trackIndex)
        discipline.setOnValueChangedListener { _, _, newVal ->
            major.init(majorList[newVal].toTypedArray())
            track.init(trackList[newVal][0].toTypedArray())
        }
        major.setOnValueChangedListener { _, _, newVal ->
            val disciplineSelector = discipline.value
            track.init(trackList[disciplineSelector][newVal].toTypedArray())
        }
    }

    fun value(): List<String> {
        val list = arrayListOf<String>()
        binding.apply {
            val disciplineValue = discipline.value
            val majorValue = major.value
            val trackValue = track.value
            list.add(disciplineList[disciplineValue])
            list.add(majorList[disciplineValue][majorValue])
            list.add(trackList[disciplineValue][majorValue][trackValue])
        }
        return list
    }
}