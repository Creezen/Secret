package com.jayce.vexis.business.kit.poker

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.client.DataTool.dpToPx
import com.google.android.material.tabs.TabLayoutMediator
import com.jayce.vexis.business.kit.poker.dialog.CheatFragment
import com.jayce.vexis.business.kit.poker.dialog.LandloadFragment
import com.jayce.vexis.business.kit.poker.dialog.RunFastFragment
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityPokerBinding
import com.jayce.vexis.databinding.PokerDialogBinding
import com.jayce.vexis.domain.bean.PokerEntry
import com.jayce.vexis.domain.enums.PokerSuit
import com.jayce.vexis.domain.viewmodel.PokerViewModel
import com.jayce.vexis.foundation.ui.StackDecorator
import com.jayce.vexis.foundation.ui.block.FlexibleDialog
import com.jayce.vexis.foundation.ui.block.TabLayoutTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokerActivity : BaseActivity<ActivityPokerBinding>() {

    private val fragments = arrayListOf<Fragment>()
    private val pokerDialogAdapter = PokerDialogAdapter(supportFragmentManager, lifecycle, fragments)
    private val pokerList: ArrayList<PokerEntry> = arrayListOf()
    private val adapter = PokerAdapter(this, pokerList)
    private val viewModel by viewModel<PokerViewModel>()

    private val cheatFragment = CheatFragment()
    private val landloadFragment = LandloadFragment()
    private val runfastFragment = RunFastFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
        loadFragment()
        showDialog()
    }

    private fun initView() {
        binding.apply {
            cards.layoutManager = LinearLayoutManager(this@PokerActivity, LinearLayoutManager.HORIZONTAL, false)
            cards.adapter = adapter
            cards.addItemDecoration(StackDecorator(58f.dpToPx().toInt()))
        }
    }

    private fun loadFragment() {
        fragments.add(landloadFragment)
        fragments.add(runfastFragment)
        fragments.add(cheatFragment)
    }

    private fun showDialog() {
        FlexibleDialog
            .flexibleView<PokerDialogBinding>(this) {
                page.adapter = pokerDialogAdapter
                TabLayoutMediator(tab, page) { tab, pos ->
                    val title = TabLayoutTitle(this@PokerActivity)
                    title.text = when (pos) {
                        0 -> "斗地主"
                        1 -> "跑得快"
                        2 -> "吹牛皮"
                        else -> ""
                    }
                    tab.customView = title
                }.attach()
                lanView.addConnectionListener(viewModel)
            }
            .cancelable(false)
            .show()
    }

    private fun initData() {
        pokerList.add(PokerEntry(PokerSuit.SPADE, 1))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 2))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 3))
        pokerList.add(PokerEntry(PokerSuit.DIAMOND, 4))
        pokerList.add(PokerEntry(PokerSuit.HEART, 4))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 6))
        pokerList.add(PokerEntry(PokerSuit.DIAMOND, 5))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 5))
        pokerList.add(PokerEntry(PokerSuit.HEART, 5))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 6))
        pokerList.add(PokerEntry(PokerSuit.CLUB, 6))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 6))
        pokerList.add(PokerEntry(PokerSuit.DIAMOND, 7))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 7))
        pokerList.add(PokerEntry(PokerSuit.HEART, 10))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 13))
        pokerList.add(PokerEntry(PokerSuit.CLUB, 12))
        pokerList.add(PokerEntry(PokerSuit.SPADE, 11))
        pokerList.add(PokerEntry(PokerSuit.SMALL_JOKER, 9))
        pokerList.add(PokerEntry(PokerSuit.BIG_JOKER, 9))
        adapter.notifyItemInserted(0)
    }
}