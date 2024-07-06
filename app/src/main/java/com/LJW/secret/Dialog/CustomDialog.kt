package com.ljw.secret.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import androidx.viewbinding.ViewBinding

class CustomDialog<T : ViewBinding>(mContext:Context, var viewBinding: T) : SimpleDialog(mContext) {

    init {
        super.binding.apply {
            replaceLayout.removeAllViews()
            replaceLayout.addView(viewBinding.root)
        }
    }

    fun setCustomLeftButton(text: String = "取消", onNegativeClick: (T, Dialog) -> Unit){
        setButton(binding.no, text, onNegativeClick)
    }

    fun setCustomRightButton(text: String = "确定", onPositiveClick: (T, Dialog) -> Unit){
        setButton(binding.yes, text, onPositiveClick)
    }

    private fun setButton(button: Button, text:String, onClick: (T, Dialog) -> Unit){
        button.visibility = View.VISIBLE
        button.setText(text)
        button.setOnClickListener { onClick(viewBinding, this) }
    }
}