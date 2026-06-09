package com.jayce.vexis.business.kit.book

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.creezen.tool.AndroidTool.msg
import com.jayce.vexis.business.kit.book.BookActivity.Companion.INIT_USER_COUNT
import com.jayce.vexis.databinding.PocketUsernameItemLayoutBinding

class BookDialogView(context: Context, attr: AttributeSet?) : LinearLayout(context, attr) {

    var binding = PocketUsernameItemLayoutBinding.inflate(
        LayoutInflater.from(context),
        this,
        true,
    )
    private var position: Int = 0
    private var func: ((Int, String) -> Unit)? = null

    init {
        binding.baseRoot = this
    }

    fun setOnButtonClick(function: (View, Int) -> Unit) {
        binding.delBtn.setOnClickListener {
            function(it, position)
        }
    }

    fun setTextChange(function: (Int, String) -> Unit) {
        this.func = function
    }

    fun afterChange(editText: EditText) {
        func?.invoke(position, editText.msg())
    }

    fun setPositon(position: Int) {
        this.position = position
        if (position < INIT_USER_COUNT) {
            binding.delBtn.visibility = View.INVISIBLE
        }
    }
}