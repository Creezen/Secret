package com.jayce.vexis.foundation.ui.block.connect

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.LanViewFirstLayoutBinding
import com.jayce.vexis.foundation.ability.socket.LanManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LanFirstPageFragment : BaseFragment<LanViewFirstLayoutBinding>(), TextWatcher, KoinComponent {

    private val manager by inject<LanManager>()

    override fun onCreateView(inflater: LayoutInflater, contain: ViewGroup?, instance: Bundle?): View {
        super.onCreateView(inflater, contain, instance)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.apply {
            manager.updateButtonText("取消", "开启监听")

            server.isChecked = true
            ipAddress.visibility = View.VISIBLE
            ipInput.visibility = View.GONE

            val ipInformation = manager.getIPInfo(requireContext())
            ipAddress.text = ipInformation

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.server -> {
                        ipAddress.visibility = View.VISIBLE
                        ipInput.visibility = View.GONE
                        manager.isServerChecked = true
                        manager.updateButtonText("取消", "开启监听")
                    }
                    R.id.client -> {
                        ipAddress.visibility = View.GONE
                        ipInput.visibility = View.VISIBLE
                        manager.isServerChecked = false
                        ipInput.requestFocus()
                        manager.updateButtonText("取消", "立即连接")
                    }
                }
            }

            ipInput.addTextChangedListener(this@LanFirstPageFragment)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /**/ }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { /**/ }

    override fun afterTextChanged(s: Editable?) {
        manager.ipInput = s.toString()
    }
}