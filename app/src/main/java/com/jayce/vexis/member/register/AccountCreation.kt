package com.jayce.vexis.member.register

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.creezen.tool.AndroidTool.init
import com.jayce.vexis.R
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.member.UserItem
import com.jayce.vexis.databinding.AccountCreationBinding
import com.jayce.vexis.stylized.SimpleDialog
import com.jayce.vexis.member.UserService
import com.creezen.tool.DataTool.getRandomString
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.DataTool.pojo2Map
import com.creezen.tool.DataTool.toTime
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountCreation : BaseActivity() {

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
        private val SEX = arrayOf("男","女","保密").toList() as ArrayList<String>
        private lateinit var EMAIL_PROFIX : ArrayList<String>
    }

    private lateinit var binding : AccountCreationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccountCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val accountName = intent.getStringExtra("intentAccount")
        if(!TextUtils.isEmpty(accountName)) binding.account.setText(accountName)
        EMAIL_PROFIX = resources.getStringArray(R.array.EmailProfix).toList() as ArrayList<String>
        initView()
    }

    private fun initView(){
        with(binding){
            val contextEnv = this@AccountCreation
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
                if (it.length !in 6 .. 18) {
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
                if (it.length !in 1 .. 8) {
                    nickNameCode = 1
                } else if (it.contains(" ")) {
                    nickNameCode = 2
                } else {
                    nickNameCode = 0
                }
                checkLoginCondition()
            }
            passwordLiveData.observe(contextEnv) {
                passwordCode = if (it.length !in 6 .. 18) 1 else 0
                checkLoginCondition()
            }
            passwordConfirmLiveData.observe(contextEnv) {
                passwordConfirmCode = if (it.equals(passwordLiveData.value)) 0 else 1
                checkLoginCondition()
            }
            introductionLiveData.observe(contextEnv) {
                introductionLength = "${it.length}/200"
            }
            yearSpinner.init(
                YEAR_DATA,
                R.layout.spinner_prompt,
                R.layout.spinner_dropdown
            )
            monthSpinner.init(
                MONTH_DATA,
                R.layout.spinner_prompt,
                R.layout.spinner_dropdown
            )
            daySpinner.init(
                DAY_DATA,
                R.layout.spinner_prompt,
                R.layout.spinner_dropdown
            )
            sex.init(
                SEX,
                R.layout.spinner_prompt,
                R.layout.spinner_dropdown
            )
            emailPostfix.init(
                EMAIL_PROFIX,
                R.layout.spinner_prompt,
                R.layout.spinner_dropdown
            )
            register.setOnClickListener {
                val dialog = SimpleDialog(this@AccountCreation).apply{
                    setTitle("提示")
                    setMessage("注册中，请稍后...")
                }
                val currentTime = System.currentTimeMillis()
                val userIdValue = "${currentTime}${getRandomString(7)}"
                val createTimeValue = currentTime.toTime("yyyy-MM-dd HH:mm:ss")
                val emailValue = if(email.msg().isNotEmpty()) "${email.msg()}${EMAIL_PROFIX[emailPostfix.selectedItemPosition]}" else ""
                val phoneValue = phone.msg()
                val addressValue = address.msg()
                val selfIntroductionValue = selfIntroduction.msg()
                val isEdit = if((emailValue.isNotEmpty()) and (phoneValue.isNotEmpty()) and (addressValue.isNotEmpty()) and (selfIntroductionValue.isNotEmpty())) 1 else 0
                val ageValue = createTimeValue.subSequence(0,4).toString().toInt()- YEAR_DATA[yearSelect]
                val birthdayValue = "${YEAR_DATA[yearSelect]}-${MONTH_DATA[monthSelected]}-${BIG_DAY[daySelected]}"
                lifecycleScope.launch(Dispatchers.Main) {
                    try {
                        val map1= UserItem(userIdValue, nickname.msg(), account.msg(), ageValue, sex.selectedItem as String,password.msg(), createTimeValue,
                            0, 0, isEdit, emailValue, selfIntroductionValue, phoneValue, addressValue, birthdayValue, ""
                        ).pojo2Map()
                        map1["type"]="1"
                        val awaitResult = NetTool.create<UserService>()
                            .checkUserName(map1)
                            .await()
                        if (null != awaitResult["status"]){
                            if(awaitResult["status"] == 2) finish()
                        } else dialog.dismiss()
                    } catch (e: Exception) {
                        e.message?.let { it1 -> Log.e("msg", it1) }
                    }
                }
            }
            register.isClickable = false
        }
    }

    fun onYearSpinnerSelect(pos: Int) {
        if (MONTH_DATA[binding.monthSelected] != 2) {
            return
        }
        if (isLeapYear(YEAR_DATA[pos])) {
            refreshDaySpinner(LEAP_DAY)
        } else {
            refreshDaySpinner(COMMON_DAY)
        }
    }

    fun onMonthSpinnerSelect(pos: Int) {
        val monthValue = MONTH_DATA[pos]
        when(monthValue) {
            1, 3, 5, 7, 8, 10, 12 -> refreshDaySpinner(BIG_DAY)
            4, 6, 9, 11 -> refreshDaySpinner(SMALL_DAY)
            else -> {
                if (isLeapYear(YEAR_DATA[binding.yearSelect])) {
                    refreshDaySpinner(LEAP_DAY)
                } else {
                    refreshDaySpinner(COMMON_DAY)
                }
            }
        }
    }

    private fun refreshDaySpinner(list: List<Int>){
        val adapter = binding.daySpinner.adapter as ArrayAdapter<Int>
        adapter.clear()
        adapter.addAll(list)
        adapter.notifyDataSetChanged()
        binding.daySelected = 0
    }

    private fun checkLoginCondition(){
        with(binding) {
            isLoginOK = (accountCode + nickNameCode + passwordCode + passwordConfirmCode) == 0
        }
    }

    private fun isLeapYear(year: Int) = when {
        year % 100 == 0 -> year%400 == 0
        year % 4 == 0 -> true
        else -> false
    }
}
