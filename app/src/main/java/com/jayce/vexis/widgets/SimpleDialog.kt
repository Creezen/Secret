package com.jayce.vexis.widgets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.jayce.vexis.R
import com.jayce.vexis.databinding.DialogLayoutBinding

open class SimpleDialog(mContext: Context) : Dialog(mContext, R.style.Dialog) {

    companion object {
        const val TITLE_VISIBLE = 0
        const val TITLE_INVISIBLE = 1
        const val LEFT_BUTTON_VISIBLE = 2
        const val LEFT_BUTTON_INVISIBLE = 3
        const val RIGHT_BUTTON_VISIBLE = 4
        const val RIGHT_BUTTON_INVISIBLE = 5
    }

    var binding:DialogLayoutBinding = DialogLayoutBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
    }

    fun setTitle(title:String){
        binding.title.text = title
    }

    fun setTitleStyle(titleStyle:(TextView) -> Unit){
        titleStyle(binding.title)
    }

    fun setMessage(message:String){
        binding.message.text = message
    }

    fun setMessgaeStyle(messageStyle:(TextView) -> Unit){
        messageStyle(binding.message)
    }

    fun setCancel(canCancel:Boolean){
        setCanceledOnTouchOutside(canCancel)
    }

    fun setLeftButton(text: String = "取消", onNegativeClick: (View, Dialog) -> Unit){
        setButton(binding.no, text, onNegativeClick)
    }

    fun setRightButton(text: String = "确定", onPositiveClick: (View, Dialog) -> Unit){
        setButton(binding.yes, text, onPositiveClick)
    }

    private fun setButton(button:Button, text:String, onClick: (View, Dialog) -> Unit){
        button.visibility = View.VISIBLE
        button.text = text
        button.setOnClickListener { onClick(binding.message,this) }
    }

    fun setVisible(viewType: Int) {
        when(viewType) {
            TITLE_VISIBLE -> binding.title.visibility = View.VISIBLE
            TITLE_INVISIBLE -> binding.title.visibility = View.GONE
            LEFT_BUTTON_VISIBLE -> binding.no.visibility = View.VISIBLE
            LEFT_BUTTON_INVISIBLE -> binding.no.visibility = View.GONE
            RIGHT_BUTTON_VISIBLE -> binding.yes.visibility = View.VISIBLE
            RIGHT_BUTTON_INVISIBLE -> binding.yes.visibility = View.GONE

        }
    }

}