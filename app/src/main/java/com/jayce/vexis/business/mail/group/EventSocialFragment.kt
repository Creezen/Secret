package com.jayce.vexis.business.mail.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.tool.TLog
import com.jayce.vexis.business.mail.OnEventDeliveryListener
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FragmentEventSocialBinding

class EventSocialFragment : BaseFragment<FragmentEventSocialBinding>(), OnEventDeliveryListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onDelivery() {
        TLog.d("onDelivery")
    }
}