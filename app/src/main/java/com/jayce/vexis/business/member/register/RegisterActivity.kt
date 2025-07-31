package com.jayce.vexis.business.member.register

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.creezen.commontool.CreezenTool.getRandomString
import com.creezen.commontool.CreezenTool.isLeapYear
import com.creezen.commontool.CreezenTool.pojo2Map
import com.creezen.commontool.CreezenTool.toTime
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.jayce.vexis.R
import com.jayce.vexis.foundation.base.BaseActivity
import com.jayce.vexis.databinding.AccountCreationBinding
import com.jayce.vexis.business.member.User
import com.jayce.vexis.business.member.UserService
import com.jayce.vexis.foundation.view.SimpleDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : BaseActivity() {

    val accountLiveData = MutableLiveData<String>()
    val nicknameLiveData = MutableLiveData<String>()
    val passwordLiveData = MutableLiveData<String>()
    val passwordConfirmLiveData = MutableLiveData<String>()
    val introductionLiveData = MutableLiveData<String>()

    companion object {
        private val YEAR_DATA = (1920..2050).toList() as ArrayList
        private val MONTH_DATA = (1..12).toList() as ArrayList
        private val DAY_DATA = ArrayList<Int>()
        private val BIG_DAY = (1..31).toList() as ArrayList
        private val SMALL_DAY = (1..30).toList() as ArrayList
        private val LEAP_DAY = (1..29).toList() as ArrayList
        private val COMMON_DAY = (1..28).toList() as ArrayList
        private val SEX = arrayOf("男", "女", "保密").toList() as ArrayList<String>
        private lateinit var emailProfix: ArrayList<String>
    }

    private lateinit var binding: AccountCreationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccountCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val accountName = intent.getStringExtra("intentAccount")
        if (!TextUtils.isEmpty(accountName)) binding.account.setText(accountName)
        emailProfix = resources.getStringArray(R.array.EmailProfix).toList() as ArrayList<String>
        initView()
    }

    private fun initView() {
        with(binding) {
            val contextEnv = this@RegisterActivity
            lifecycleOwner = contextEnv
            activity = contextEnv
            accountCode = 1
            nickNameCode = 1
            passwordCode = 1
            passwordConfirmCode = 1
            introductionLength = "0/200"
            yearSelect = 0
            monthSelected = 0
            daySelected = 0

            accountLiveData.observe(contextEnv) {
                if (it.length !in 6..18) {
                    accountCode = 1
                    return@observe
                }
                accountCode = 4
                val map = HashMap<String, String>().also { hashmap ->
                    hashmap["type"] = "0"
                    hashmap["name"] = it
                }
                lifecycleScope.launch(Dispatchers.Main) {
                    val result = NetTool.create<UserService>()
                        .checkUserName(map)
                        .await()
                    if (result["status"] == 0) {
                        accountCode = 0
                        return@launch
                    }
                    accountCode = 2
                }
                checkLoginCondition()
            }
            nicknameLiveData.observe(contextEnv) {
                nickNameCode = 1
                if (it.length !in 1 ..8) {
                    nickNameCode = 1
                } else if (it.contains(" ")) {
                    nickNameCode = 2
                } else {
                    nickNameCode = 0
                }
                checkLoginCondition()
            }
            passwordLiveData.observe(contextEnv) {
                passwordCode = if (it.length !in 6..18) 1 else 0
                checkLoginCondition()
            }
            passwordConfirmLiveData.observe(contextEnv) {
                passwordConfirmCode = if (it.equals(passwordLiveData.value)) 0 else 1
                checkLoginCondition()
            }
            introductionLiveData.observe(contextEnv) {
                introductionLength = "${it.length}/200"
            }
            yearSpinner.configuration(YEAR_DATA) {
                val item = MONTH_DATA[monthSelected]
                val source = if (item == 2) {
                    if (isLeapYear(YEAR_DATA[it])) {
                        LEAP_DAY
                    } else {
                        COMMON_DAY
                    }
                } else {
                    if (item in listOf(4, 6, 9, 11)) {
                        SMALL_DAY
                    } else {
                        BIG_DAY
                    }
                }
                daySpinner.refreshData(source)
            }
            monthSpinner.configuration(MONTH_DATA) {
                when (MONTH_DATA[it]) {
                    2 -> {
                        if (isLeapYear(YEAR_DATA[yearSelect])) {
                            daySpinner.refreshData(LEAP_DAY)
                        } else {
                            daySpinner.refreshData(COMMON_DAY)
                        }
                    }
                    4, 6, 9, 11 -> daySpinner.refreshData(SMALL_DAY)
                    else -> daySpinner.refreshData(BIG_DAY)
                }
            }
            DAY_DATA.clear()
            DAY_DATA.addAll(BIG_DAY)
            daySpinner.configuration(DAY_DATA)
            sex.configuration(SEX)
            emailPostfix.configuration(emailProfix)
            register.setOnClickListener {
                val dialog = SimpleDialog(this@RegisterActivity).apply {
                        setTitle("提示")
                        setMessage("注册中，请稍后...")
                    }
                val currentTime = System.currentTimeMillis()
                val userIdValue = "${currentTime}${getRandomString(7)}"
                val createTimeValue = currentTime.toTime("yyyy-MM-dd HH:mm:ss")
                val emailValue = if (email.msg().isNotEmpty()) "${email.msg()}${emailProfix[emailPostfix.selectedItemPosition]}" else ""
                val phoneValue = phone.msg()
                val addressValue = address.msg()
                val selfIntroductionValue = selfIntroduction.msg()
                val isEdit = if (
                    (emailValue.isNotEmpty())
                    and (phoneValue.isNotEmpty())
                    and (addressValue.isNotEmpty())
                    and (selfIntroductionValue.isNotEmpty())
                        ) {
                        1
                    } else {
                        0
                    }
                val ageValue = createTimeValue.subSequence(0, 4).toString().toInt() - YEAR_DATA[yearSelect]
                val birthdayValue = "${YEAR_DATA[yearSelect]}-${MONTH_DATA[monthSelected]}-${BIG_DAY[daySelected]}"
                lifecycleScope.launch(Dispatchers.Main) {
                    try {
                        val map1= User(
                            userIdValue,
                            nickname.msg(),
                            account.msg(),
                            ageValue,
                            sex.selectedItem as String,
                            password.msg(),
                            createTimeValue,
                            0,
                            0,
                            isEdit,
                            emailValue,
                            selfIntroductionValue,
                            phoneValue,
                            addressValue,
                            birthdayValue,
                            "",
                        ).pojo2Map()
                        map1["type"] = "1"
                        val awaitResult = NetTool.create<UserService>()
                                .checkUserName(map1)
                                .await()
                        if (null != awaitResult["status"]) {
                            if (awaitResult["status"] == 2) finish()
                        } else {
                            dialog.dismiss()
                        }
                    } catch (e: Exception) {
                        e.message?.let { it1 -> Log.e("msg", it1) }
                    }
                }
            }
            register.isClickable = false
        }
    }

    private fun checkLoginCondition() {
        with(binding) {
            isLoginOK = (accountCode + nickNameCode + passwordCode + passwordConfirmCode) == 0
        }
    }
}