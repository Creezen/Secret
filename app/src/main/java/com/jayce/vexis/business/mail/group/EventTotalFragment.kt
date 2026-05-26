package com.jayce.vexis.business.mail.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.business.mail.MailEventAdapter
import com.jayce.vexis.business.mail.OnEventDeliveryListener
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FragmentEventAllBinding
import com.jayce.vexis.domain.bean.EventEntry
import com.jayce.vexis.domain.viewmodel.MailViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class EventTotalFragment : BaseFragment<FragmentEventAllBinding>(), OnEventDeliveryListener {

    private val viewModel by activityViewModel<MailViewModel>()

    private val list = listOf<EventEntry>()
    private val adapter = MailEventAdapter(list)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initView()
        return binding.root
    }

    private fun initView() = binding.apply {
        rvTotal.layoutManager = LinearLayoutManager(context)
        rvTotal.adapter = adapter
    }

    override fun onDelivery() {
        val list = viewModel.getTotalEventList()
        adapter.notifyDataChange(list)
    }
}