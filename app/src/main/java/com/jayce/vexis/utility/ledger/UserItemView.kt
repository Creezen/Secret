package com.jayce.vexis.utility.ledger

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import com.jayce.vexis.R
import com.jayce.vexis.databinding.PocketUsernameItemLayoutBinding
import com.creezen.tool.AndroidTool.addTextChangedListener
import com.jayce.vexis.utility.ledger.MainMenu.Companion.INIT_USER_COUNT

class UserItemView: LinearLayout{

    private var bind: PocketUsernameItemLayoutBinding
    var edit: EditText
    var button: ImageButton
    private var position: Int = 0
    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle){
        bind = PocketUsernameItemLayoutBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.pocket_username_item_layout, this)
        )
        edit = bind.nameEdt
        button = bind.delBtn

    }

    fun setOnButtonClick(function:(View, Int) -> Unit){
        bind.delBtn.setOnClickListener {
            function(it, position)
        }
    }

    fun setOnTextChange(function: (Int, String)-> Unit) {
        bind.nameEdt.addTextChangedListener {
            afterTextChanged {
                function(position, it.toString())
            }
        }
    }

    fun setPositon(position: Int) {
        this.position = position
        if (position < INIT_USER_COUNT) button.visibility = View.INVISIBLE
    }

}