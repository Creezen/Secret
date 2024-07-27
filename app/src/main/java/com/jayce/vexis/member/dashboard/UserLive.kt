package com.jayce.vexis.member.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.BaseFragment
import com.jayce.vexis.databinding.UserLiveBinding

class UserLive: BaseFragment() {

    private lateinit var binding: UserLiveBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = UserLiveBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView() {

    }

}