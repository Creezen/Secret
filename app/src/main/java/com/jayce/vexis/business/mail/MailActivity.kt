package com.jayce.vexis.business.mail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.creezen.commontool.Config.NIL
import com.creezen.tool.ThreadTool.ui
import com.google.android.material.tabs.TabLayoutMediator
import com.jayce.vexis.business.mail.group.EventFeedbackFragment
import com.jayce.vexis.business.mail.group.EventRoleFragment
import com.jayce.vexis.business.mail.group.EventSocialFragment
import com.jayce.vexis.business.mail.group.EventTotalFragment
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityMailBinding
import com.jayce.vexis.domain.viewmodel.MailViewModel
import com.jayce.vexis.foundation.ui.block.TabLayoutTitle
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MailActivity : BaseActivity<ActivityMailBinding>() {

    private val eventTotalFragment: EventTotalFragment
    private val eventSocialFragment: EventSocialFragment
    private val eventFeedbackFragment: EventFeedbackFragment
    private val eventRoleFragment: EventRoleFragment
    private val fragmentList = arrayListOf<Fragment>()
    private val mailAdapter: MailAdapter
    private val viewModel by viewModel<MailViewModel>()

    private var lastReadId: Long = -1

    init {
        eventTotalFragment = EventTotalFragment()
        eventSocialFragment = EventSocialFragment()
        eventFeedbackFragment = EventFeedbackFragment()
        eventRoleFragment = EventRoleFragment()
        fragmentList.add(eventTotalFragment)
        fragmentList.add(eventSocialFragment)
        fragmentList.add(eventFeedbackFragment)
        fragmentList.add(eventRoleFragment)
        mailAdapter = MailAdapter(supportFragmentManager, lifecycle, fragmentList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() = binding.apply {
        page.adapter = mailAdapter
        TabLayoutMediator(tab, page) { tab, pos ->
            val textView = TabLayoutTitle(this@MailActivity)
            textView.text = when (pos) {
                0 -> "全部"
                1 -> "社交通知"
                2 -> "反馈通知"
                3 -> "账号通知"
                else -> NIL
            }
            tab.customView = textView
        }.attach()

        viewModel.registerOnDeliveryListener(eventTotalFragment)
        viewModel.registerOnDeliveryListener(eventSocialFragment)
        viewModel.registerOnDeliveryListener(eventFeedbackFragment)
        viewModel.registerOnDeliveryListener(eventRoleFragment)
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.mailFlow.onSubscription {
                val pair = viewModel.getMailEvent()
                pair.first.forEach {
                    viewModel.deliveryEvent(it)
                }
                if (lastReadId < pair.second) lastReadId = pair.second
            }.collect { ui {
                if (it.id <= lastReadId) return@ui
                viewModel.deliveryEvent(it)
            } }
        }
    }
}