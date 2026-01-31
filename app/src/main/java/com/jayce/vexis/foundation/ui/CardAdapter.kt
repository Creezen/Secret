package com.jayce.vexis.foundation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.creezen.tool.DataTool.dpToPx
import com.jayce.vexis.databinding.CardItemLayoutBinding

abstract class CardAdapter<T: ViewBinding, H: CardAdapter.ViewHolder>(
    private val list: List<Any>
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    var elevation: Float = -1f
        set(value) {
            field = value.dpToPx()
        }

    var maxElevation: Float = -1f
        set(value) {
            field = value.dpToPx()
        }

    var cornerRadius: Float = -1f
        set(value) {
            field = value.dpToPx()
        }

    var cardPadding: Int = -1
        set(value) {
            field = value.toFloat().dpToPx().toInt()
        }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardItemLayoutBinding.inflate(inflater, parent, false)
        val pair = getChildAndHoler(inflater, binding, binding.container)
        binding.container.removeAllViews()
        binding.container.addView(pair.first.root)
        return pair.second
    }

    override fun getItemViewType(position: Int) = position

    final override fun getItemCount() = list.size

    final override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardView = holder.binding.base
        if (elevation > 0) {
            cardView.cardElevation = elevation
        }
        if (maxElevation > 0) {
            cardView.maxCardElevation = maxElevation
        }
        if (cornerRadius > 0) {
            cardView.radius = cornerRadius
        }
        if (cardPadding > 0) {
            cardView.setContentPadding(cardPadding, cardPadding, cardPadding, cardPadding)
        }
        bindCardViewHolder(holder as H, position)
    }

    abstract fun bindCardViewHolder(holder: H, position: Int)

    abstract fun getChildAndHoler(
        layoutInflater: LayoutInflater,
        containerBinding: CardItemLayoutBinding,
        parent: ViewGroup
    ) : Pair<T, H>

    abstract class ViewHolder(
        val binding: CardItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root)
}