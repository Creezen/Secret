package com.ljw.secret.Dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.ljw.secret.R
import com.ljw.secret.databinding.DialogLayoutBinding

open class SimpleDialog(mContext: Context) : Dialog(mContext, R.style.Dialog) {

    var binding:DialogLayoutBinding

    init {
        binding = DialogLayoutBinding.inflate(layoutInflater)
        setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun setTitle(title:String){
        binding.title.setText(title)
    }

    fun setTitleStyle(titleStyle:(TextView) -> Unit){
        titleStyle(binding.title)
    }

    fun setMessage(message:String){
        binding.message.setText(message)
    }

    fun setMessgaeStyle(messageStyle:(TextView) -> Unit){
        messageStyle(binding.message)
    }

    fun setCanel(canCancel:Boolean){
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
        button.setText(text)
        button.setOnClickListener { onClick(binding.message,this) }
    }

}