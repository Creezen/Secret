package com.jayce.vexis.business.kit.poker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.DataTool.dpToPx
import com.jayce.vexis.R
import com.jayce.vexis.databinding.CardItemLayoutBinding
import com.jayce.vexis.databinding.PokerItemBinding
import com.jayce.vexis.domain.bean.PokerEntry
import com.jayce.vexis.foundation.ui.CardAdapter

class PokerAdapter(
    private val context: Context,
    private var list: List<PokerEntry>
) : CardAdapter<PokerEntry, PokerItemBinding, PokerAdapter.ViewHolder>(list) {

    class ViewHolder(
        containBnding: CardItemLayoutBinding,
        binding: PokerItemBinding
    ) : CardAdapter.ViewHolder(containBnding) {
        val top = binding.top
        val bottom = binding.bottom
        val view = binding.root
        val bind = containBnding
    }

    override fun getItemCount() = list.size

    override fun getAttachedList() = list

    override fun updateAttachedList(newList: List<PokerEntry>) {
        list = newList
    }

    override fun bindCardViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.top.text = item.rankChar
        holder.bottom.text = item.rankChar
        val size = if (item.isJoker()) 20f else 32f
        holder.top.textSize = size
        val suit = item.suit
        holder.top.setTextColor(context.getColor(suit.colorId))
        holder.bottom.setTextColor(context.getColor(suit.colorId))
        if (!item.isJoker()) {
            holder.top.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, suit.resId)
            holder.bottom.setCompoundDrawablesWithIntrinsicBounds(0, suit.resId, 0, 0)
        }
        holder.bottom.textSize = size
        holder.view.setOnClickListener {
            item.rank.toast()
            val offset = if (item.isSelect) 16f else -16f
            item.isSelect = item.isSelect.not()
            holder.bind.root.y += offset.dpToPx()
        }
    }

    override fun getChildAndHoler(
        viewType: Int,
        layoutInflater: LayoutInflater,
        containerBinding: CardItemLayoutBinding,
        parent: ViewGroup
    ): Pair<PokerItemBinding, ViewHolder> {
        containerBinding.base.setBackgroundResource(R.drawable.poker)
        containerBinding.base.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        val childBind = PokerItemBinding.inflate(layoutInflater, parent, false)
        val holder = ViewHolder(containerBinding, childBind)
        return childBind to holder
    }
}