package com.jayce.vexis.foundation.ui.block

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.jayce.vexis.R
import com.jayce.vexis.databinding.InformationCardLayoutBinding

class BasicInformationCard(context: Context, attr: AttributeSet) : CardView(context, attr) {

    var cardText: String = ""
        set(value) {
            field = value
            binding.mainText.text = value
        }

    var cardDescription: String = ""
        set(value) {
            field = value
            binding.description.text = value
        }

    private val binding: InformationCardLayoutBinding =
        InformationCardLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val text = binding.mainText
        text.isSelected = true
        text.paintFlags = text.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        context.obtainStyledAttributes(attr, R.styleable.BasicInformationCard).use {
            cardText = it.getString(R.styleable.BasicInformationCard_cardText) ?: "0"
            cardDescription = it.getString(R.styleable.BasicInformationCard_cardDescription) ?: ""
        }
    }

}