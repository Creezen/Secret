package com.jayce.vexis.foundation.ui.block

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import com.jayce.vexis.databinding.TabTitleItemBinding

class TabLayoutTitle(context: Context) : AppCompatTextView(context) {

    init {
        TabTitleItemBinding.inflate(LayoutInflater.from(context))
    }

}