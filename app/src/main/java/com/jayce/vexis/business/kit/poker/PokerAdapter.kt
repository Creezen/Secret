package com.jayce.vexis.business.kit.poker

import android.view.LayoutInflater
import android.view.ViewGroup
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.DataTool.dpToPx
import com.jayce.vexis.R
import com.jayce.vexis.databinding.CardItemLayoutBinding
import com.jayce.vexis.databinding.PokerItemBinding
import com.jayce.vexis.domain.bean.PokerEntry
import com.jayce.vexis.domain.enums.PokerSuit
import com.jayce.vexis.foundation.view.CardAdapter

class PokerAdapter(
    private val list: List<PokerEntry>
) : CardAdapter<PokerItemBinding, PokerAdapter.ViewHolder>(list) {

    class ViewHolder(
        containBnding: CardItemLayoutBinding,
        binding: PokerItemBinding
    ) : CardAdapter.ViewHolder(containBnding) {
        val top = binding.top
        val bottom = binding.bottom
        val view = binding.root
        val bind = containBnding
    }

    override fun bindCardViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.top.text = item.rankChar
        holder.bottom.text = item.rankChar
        val size = if (item.isJoker()) 20f else 32f
        holder.top.textSize = size
        holder.bottom.textSize = size
//        holder.view.translationZ = position.toFloat()
        holder.view.setOnClickListener {
            item.rank.toast()
            val offset = if (item.isSelect) 16f else -16f
            item.isSelect = item.isSelect.not()
            holder.bind.root.y += offset.dpToPx()
        }
    }

    override fun getChildAndHoler(
        layoutInflater: LayoutInflater,
        containerBinding: CardItemLayoutBinding,
        parent: ViewGroup
    ): Pair<PokerItemBinding, ViewHolder> {
        containerBinding.base.setBackgroundResource(R.drawable.poker)
        containerBinding.base.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val childBind = PokerItemBinding.inflate(layoutInflater, parent, false)
        val holder = ViewHolder(containerBinding, childBind)
        return childBind to holder
    }
}