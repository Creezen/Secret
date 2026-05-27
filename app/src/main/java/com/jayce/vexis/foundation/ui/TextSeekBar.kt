package com.jayce.vexis.foundation.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.creezen.tool.AndroidTool.intMsg
import com.jayce.vexis.databinding.TaxtSeekbarLayoutBinding

class TextSeekBar(context: Context, attr: AttributeSet) :
    FrameLayout(context, attr), OnSeekBarChangeListener, TextWatcher {

    private val binding = TaxtSeekbarLayoutBinding.inflate(LayoutInflater.from(context), this)
    private var textSeekbarChangeListener: OnTextSeekbarChangeListener? = null

    init {
        binding.startText.addTextChangedListener(this)
    }

    var progress: Int = 0
        get() = binding.seekbar.progress
        set(value) {
            field = value
            binding.seekbar.progress = value
            if (value == binding.startText.intMsg(-1)) return
            val setValue = value.toString()
            binding.startText.setText(setValue)
        }

    var max: Int = 100
        get() = binding.seekbar.max
        set(value) {
            binding.seekbar.max = value
            field = value
        }

    fun setOnSeekBarChangeListener(listener: OnTextSeekbarChangeListener) {
        textSeekbarChangeListener = listener
        binding.seekbar.setOnSeekBarChangeListener(this)
    }

    fun setStartText(text: String) {
        binding.startText.setText(text)
    }

    fun setEndText(text: String) {
        binding.endText.text = text
    }

    interface OnTextSeekbarChangeListener {
        fun onTextSeekbarChange(seekBar: TextSeekBar, progress: Int, fromUser: Boolean)

        fun onStartTextSeekbarTouch(seekBar: TextSeekBar)

        fun onStopTextSeekbarTouch(seekBar: TextSeekBar)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        setStartText(progress.toString())
        textSeekbarChangeListener?.onTextSeekbarChange(this, progress, fromUser)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        textSeekbarChangeListener?.onStartTextSeekbarTouch(this)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        textSeekbarChangeListener?.onStopTextSeekbarTouch(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /**/ }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { /**/ }

    override fun afterTextChanged(s: Editable?) {
        if (s.isNullOrEmpty()) {
            binding.startText.setText("0")
            progress = 0
            return
        }
        binding.startText.setSelection(s.length)
        var currentProgress = s.toString().toInt()
        if (currentProgress > max) currentProgress = max
        progress = currentProgress
    }
}