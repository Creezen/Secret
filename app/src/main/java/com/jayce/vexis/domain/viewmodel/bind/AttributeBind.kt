package com.jayce.vexis.domain.viewmodel.bind

import androidx.databinding.BindingAdapter
import com.jayce.vexis.foundation.ui.block.HintView

@BindingAdapter("showIcon")
fun setIconVisible(hintView: HintView, isShow: Boolean) {
    hintView.setIconVisible(isShow)
}

@BindingAdapter("iconText")
fun setIconHintText(hintView: HintView, string: String) {
    hintView.setIconHintText(string)
}