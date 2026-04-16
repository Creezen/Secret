package com.jayce.vexis.business.kit.poker

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.DataTool.dpToPx
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityPokerBinding
import com.jayce.vexis.domain.bean.PokerEntry
import com.jayce.vexis.domain.enums.PokerSuit
import com.jayce.vexis.foundation.ui.StackDecorator

class PokerActivity : BaseActivity<ActivityPokerBinding>() {

    private val pokerList: ArrayList<PokerEntry> = arrayListOf()
    private val adapter = PokerAdapter(pokerList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        binding.apply {
            cards.layoutManager = LinearLayoutManager(this@PokerActivity, LinearLayoutManager.HORIZONTAL, false)
            cards.adapter = adapter
            cards.addItemDecoration(StackDecorator(60f.dpToPx().toInt()))
        }
    }

    private fun initData() {
        pokerList.add(PokerEntry(PokerSuit.SPADE, 1))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 2))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 3))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 4))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 4))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 6))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 5))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 5))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 5))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 6))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 6))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 6))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 7))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 7))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 10))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 13))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 12))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 11))
        pokerList.add(PokerEntry(PokerSuit.SMALL_JOKER, 9))
        pokerList.add(PokerEntry(PokerSuit.BIG_JOKER, 9))
        adapter.notifyDataSetChanged()
    }
}