package com.jayce.vexis.business.profile.dashboard.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.StatusManager.liveUser
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.UserBasicInfoBinding

class UserBasicInfoFragment : BaseFragment<UserBasicInfoBinding>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        initView()
        return binding.root
    }

    private fun initView() = binding.apply {
        liveUser.let {
            createTime.text = it.createTime
            sex.text = it.profile.sex
            age.text = "${it.profile.age}"
            count.text = "${it.privilege.count}"
            birthday.text = it.profile.birthday
            phone.text = it.profile.phone
            email.text = it.profile.email
            address.text = it.profile.address
            selfIntroduction.text = it.profile.selfIntroduction
            isEdit.visibility = View.GONE
        }
    }
}