package com.jayce.vexis.business.mail.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.business.mail.MailEventAdapter
import com.jayce.vexis.business.mail.OnEventDeliveryListener
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FragmentEventFeedbackBinding
import com.jayce.vexis.domain.bean.EventEntry
import com.jayce.vexis.domain.viewmodel.MailViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class EventFeedbackFragment : BaseFragment<FragmentEventFeedbackBinding>(), OnEventDeliveryListener {

    private val viewModel by activityViewModel<MailViewModel>()

    private val list = listOf<EventEntry>()
    private val adapter = MailEventAdapter(list)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initView()
        return binding.root
    }

    private fun initView() = binding.apply {
        mailFeedbackRv.layoutManager = LinearLayoutManager(context)
        mailFeedbackRv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        notifyDataChange()
    }

    override fun onDelivery() {
        view?.post { notifyDataChange() }
    }

    private fun notifyDataChange() {
        val list = viewModel.getFeedbackEventList()
        adapter.notifyDataChange(list)
    }
}