package com.jayce.vexis.business.member.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.UserBasicInfoBinding
import com.jayce.vexis.core.base.BaseViewModel

class UserBasicInfoFragment : BaseFragment<BaseViewModel>() {

    private lateinit var binding: UserBasicInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = UserBasicInfoBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView() {
        with(binding) {
            user().let {
                createTime.text = it.createTime
                sex.text = it.sex
                age.text = "${it.age}"
                count.text = "${it.count}"
                birthday.text = it.birthday
                phone.text = it.phone
                email.text = it.email
                address.text = it.address
                selfIntroduction.text = it.selfIntroduction
                isEdit.visibility = View.GONE
            }
        }
    }
}