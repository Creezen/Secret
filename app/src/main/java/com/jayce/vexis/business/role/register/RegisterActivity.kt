package com.jayce.vexis.business.role.register

import android.os.Bundle
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.BirthdaySelectorBinding
import com.jayce.vexis.databinding.CreateRoleLayoutBinding
import com.jayce.vexis.databinding.DialogBinding
import com.jayce.vexis.foundation.view.block.FlexibleDialog
import com.jayce.vexis.foundation.viewmodel.RegisterViewModel
import org.koin.android.ext.android.inject

class RegisterActivity : BaseActivity<CreateRoleLayoutBinding>() {

    private val registerViewModel by inject<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    private fun initViewModel() {
        val owner = this
        registerViewModel.apply {
            initStatus(resources, intent.getStringExtra("intentAccount"))
            roleId.observe(owner) {
                handleRoleId(it)
            }
            nickname.observe(owner) {
                handleNickName(it)
            }
            password.observe(owner) {
                handlePassword(it)
            }
            confirmPassword.observe(owner) {
                handleConfirmPassword(it)
            }
            bio.observe(owner) {
                handleBio(it)
            }
        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = this@RegisterActivity
            vm = registerViewModel
            birthdayLayout.setOnClickListener {
                FlexibleDialog<BirthdaySelectorBinding>(
                    this@RegisterActivity,
                    layoutInflater
                )
                .flexibleView(BirthdaySelectorBinding::inflate)
                .title("选择您的出生日期")
                .positive("确定") {
                    registerViewModel.handleBirthday(birthday.time())
                    return@positive 1
                }.show()
            }
            sex.init(registerViewModel.sexList)
            emailPostfix.init(registerViewModel.emailSuffix)
            registerBtn.setOnClickListener {
                val dialog = FlexibleDialog<DialogBinding>(
                    this@RegisterActivity,
                    layoutInflater
                )
                .title("提示")
                .flexibleView {
                    message.text = "注册中，请稍后..."
                }.show()
                registerViewModel.registerRole { result ->
                    if (result) {
                        finish()
                    } else {
                        dialog.dismiss()
                    }
                }
            }
        }
    }
}