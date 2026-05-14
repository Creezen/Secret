package com.jayce.vexis.business.kit.gomoku.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.GomokuDialogFirstLayoutBinding
import com.jayce.vexis.domain.viewmodel.GomokuViewModel

class MenuFirstPageFragment : BaseFragment<GomokuDialogFirstLayoutBinding>(), TextWatcher {

    private val viewModel by activityViewModels<GomokuViewModel>()

    override fun onCreateView(inflater: LayoutInflater, contain: ViewGroup?, instance: Bundle?): View {
        super.onCreateView(inflater, contain, instance)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.apply {
            viewModel.updateButtonText("取消" to "开启监听")

            server.isChecked = true
            ipAddress.visibility = View.VISIBLE
            ipInput.visibility = View.GONE

            val ipInformation = viewModel.getIPInfo(requireContext())
            ipAddress.text = ipInformation

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.server -> {
                        ipAddress.visibility = View.VISIBLE
                        ipInput.visibility = View.GONE
                        viewModel.isServerChecked = true
                        viewModel.updateButtonText("取消" to "开启监听")
                    }
                    R.id.client -> {
                        ipAddress.visibility = View.GONE
                        ipInput.visibility = View.VISIBLE
                        viewModel.isServerChecked = false
                        ipInput.requestFocus()
                        viewModel.updateButtonText("取消" to "立即连接")
                    }
                }
            }

            ipInput.addTextChangedListener(this@MenuFirstPageFragment)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /**/ }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { /**/ }

    override fun afterTextChanged(s: Editable?) {
        viewModel.ipInput = s.toString()
    }
}