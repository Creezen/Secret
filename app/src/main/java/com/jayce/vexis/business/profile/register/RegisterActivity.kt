package com.jayce.vexis.business.profile.register

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.jayce.vexis.R
import com.jayce.vexis.client.AndroidTool.init
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.BirthdaySelectorBinding
import com.jayce.vexis.databinding.CreateRoleLayoutBinding
import com.jayce.vexis.databinding.RegisterEmailCodeBinding
import com.jayce.vexis.databinding.RegisterEmailSeletorBinding
import com.jayce.vexis.domain.viewmodel.RegisterViewModel
import com.jayce.vexis.foundation.ui.block.FlexibleDialog
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RegisterActivity : BaseActivity<CreateRoleLayoutBinding>() {

    private val emailArray by lazy { resources.getStringArray(R.array.emailType) }
    private val model by inject<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    private fun initViewModel() {
        val owner = this
        model.apply {
            nickname.observe(owner) { handleNickName(it) }
            password.observe(owner) { handlePassword(it) }
            confirmPassword.observe(owner) { handleConfirmPassword(it) }
            emailContent.observe(owner) { handleEmailContent(it) }
            bio.observe(owner) { handleBio(it) }
        }
        lifecycleScope.launch {
            model.emailFlow.collect { showCodeDialog(it) }
        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = this@RegisterActivity
            vm = model
            birthdayLayout.setOnClickListener {
                FlexibleDialog.flexibleView<BirthdaySelectorBinding>(this@RegisterActivity)
                    .title("选择您的出生日期")
                    .positive("确定") { model.handleBirthday(birthday.time()) }
                    .show()
            }
            sex.init(model.sexList)
            emailPostfix.setOnClickListener {
                FlexibleDialog
                    .flexibleView<RegisterEmailSeletorBinding>(this@RegisterActivity) {
                        selector.init(emailArray)
                    }
                    .title("选择您的邮箱")
                    .positive("确定") {
                        emailPostfix.text = emailArray[selector.value]
                    }
                    .show()
            }
            model.emailType.value = emailArray[0]
            registerBtn.setOnClickListener { model.sendEmail() }
        }
    }

    private fun showCodeDialog(pair: Pair<String, String>) {
        val id = pair.first
        val email = pair.second
        FlexibleDialog.flexibleView<RegisterEmailCodeBinding>(this@RegisterActivity)
            .title("请输入验证码 ($id)")
            .cancelable(false)
            .negative("取消")
            .positive("立即注册", false) { dialog ->
                model.registerRole(id, email, emailCode.msg()) {
                    dialog.dismiss()
                    finish()
                    "注册成功".toast()
                }
            }
            .show()
    }
}