package com.jayce.vexis.business.member.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.UserLiveBinding
import com.jayce.vexis.core.base.BaseViewModel

class UserLiveFragment : BaseFragment<BaseViewModel>() {

    private lateinit var binding: UserLiveBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = UserLiveBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView() {}
}