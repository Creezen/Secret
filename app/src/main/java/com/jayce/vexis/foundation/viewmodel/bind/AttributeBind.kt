package com.jayce.vexis.foundation.viewmodel.bind

import androidx.databinding.BindingAdapter
import com.jayce.vexis.foundation.view.block.HintView

@BindingAdapter("showIcon")
fun setIconVisible(hintView: HintView, isShow: Boolean) {
    hintView.setIconVisible(isShow)
}

@BindingAdapter("iconText")
fun setIconHintText(hintView: HintView, string: String) {
    hintView.setIconHintText(string)
}