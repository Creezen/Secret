package com.jayce.vexis.business.kit.book

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.databinding.PocketUsernameItemLayoutBinding

class BookDialogView(context: Context, attr: AttributeSet?) : LinearLayout(context, attr) {

    private val inflater = LayoutInflater.from(context)
    private val binding = PocketUsernameItemLayoutBinding.inflate(inflater, this, true)
    private var position: Int = 0
    private var func: ((Int, String) -> Unit)? = null

    init { binding.root = this }

    fun setOnItemDelete(function: (View, Int) -> Unit) {
        binding.delete.setOnClickListener {
            function(it, position)
        }
    }

    fun setTextChange(function: (Int, String) -> Unit) {
        this.func = function
    }

    fun afterChange(editText: EditText) {
        func?.invoke(position, editText.msg())
    }

    fun refresh(hints: String, name: String = "", position: Int = -1) {
        binding.playerName.apply {
            hint = hints
            if (name.isNotEmpty()) { setText(name) }
            if (position >= 0) this@BookDialogView.position = position
        }
    }

    fun enableDelete(shouldDelete: Boolean) {
        binding.delete.visibility = if (shouldDelete) View.VISIBLE else GONE
    }

    fun content() = binding.playerName.msg()
}