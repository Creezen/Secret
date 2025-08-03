package com.jayce.vexis.business.kit.ledger

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.creezen.tool.AndroidTool.msg
import com.jayce.vexis.databinding.PocketUsernameItemLayoutBinding
import com.jayce.vexis.business.kit.ledger.LedgerSheetActivity.Companion.INIT_USER_COUNT

class ScoreInsertEntryView(
    context: Context,
    attr: AttributeSet?,
) : LinearLayout(context, attr) {
    var binding: PocketUsernameItemLayoutBinding
    private var position: Int = 0
    private var func: ((Int, String) -> Unit)? = null

    init {
        binding = PocketUsernameItemLayoutBinding.inflate(
                LayoutInflater.from(context),
                this,
                true,
            )
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