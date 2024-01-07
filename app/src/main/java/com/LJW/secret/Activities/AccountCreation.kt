package com.ljw.secret.activities

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.ljw.secret.dialog.SimpleDialog
import com.ljw.secret.network.UserService
import com.ljw.secret.NetworkServerCreator
import com.ljw.secret.pojo.User
import com.ljw.secret.POJO2Map
import com.ljw.secret.R
import com.ljw.secret.addTextChangedListener
import com.ljw.secret.await
import com.ljw.secret.config
import com.ljw.secret.databinding.AccountCreationBinding
import com.ljw.secret.getRandomString
import com.ljw.secret.msg
import com.ljw.secret.toTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountCreation : BaseActivity() {

    private var selectedMonth=1

    private var nameFlag = false
    private var nicknameFlag = false
    private var passwordFlag = false
    private var passwordConfirmFlag = false

    companion object {
        private val YEAR= (1920..2050).toList() as ArrayList
        private val MONTH= (1..12).toList() as ArrayList
        private val DAY=ArrayList<Int>()
        private val BIG_DAY= (1..31).toList() as ArrayList
        private val SMALL_DAY= (1..30).toList() as ArrayList
        private val LEAP_DAY= (1..29).toList() as ArrayList
        private val COMMON_DAY= (1..28).toList() as ArrayList
        private val SEX= arrayOf("男","女","保密").toList() as ArrayList<String>
        private lateinit var EMAIL_PROFIX:ArrayList<String>
    }

    private lateinit var binding:AccountCreationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=AccountCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val accountName= intent.getStringExtra("intentAccount")
        if(!TextUtils.isEmpty(accountName)) binding.name.setText(accountName)
        EMAIL_PROFIX = resources.getStringArray(R.array.EmailProfix).toList() as ArrayList<String>
        pageInit()
    }

    private fun pageInit(){
        with(binding){
            name.configAfterText {
                if(it.length in 6..18){
                    nameHintLength.visibility=View.GONE
                    val map=HashMap<String,String>()
                    map["type"] = "0"
                    map["name"] = name.msg()
                    CoroutineScope(Dispatchers.Main).launch{
                        try {
                            val awaitResult = NetworkServerCreator.create<UserService>()
                                .checkUserName(map)
                                .await()
                            if (awaitResult["status"] == 0) {
                                name.setTextColor(Color.parseColor("#00ff00"))
                                nameHIntExist.visibility = View.GONE
                                nameFlag = true
                            } else {
                                name.setTextColor(Color.parseColor("#000000"))
                                nameHIntExist.visibility = View.VISIBLE
                                nameFlag = false
                            }
                            checkInformation(register,registerHint)
                        } catch (e: Exception) {
                            e.message?.let { it1 -> Log.e("msg", it1) }
                        }
                    }
                } else {
                    nameHIntExist.visibility=View.GONE
                    nameHintLength.visibility=View.VISIBLE
                    name.setTextColor(Color.parseColor("#000000"))
                    nameFlag=false
                }
            }
            nickname.configAfterText {
                nicknameHintLength.visibility=if (it.length in 1..8) View.GONE else View.VISIBLE
                nicknameHintValid.visibility=if (" " in it) View.VISIBLE else View.GONE
                nicknameFlag = if (it.length in 1..8){
                    this.setTextColor(Color.parseColor("#00ff00"))
                    true
                }else{
                    this.setTextColor(Color.parseColor("#000000"))
                    false
                }
            }
            password.configAfterText {
                if (it.length in 6..18){
                    passwordHintLength.visibility=View.GONE
                    passwordFlag=true
                }else{
                    passwordHintLength.visibility=View.VISIBLE
                    passwordFlag=false
                }
                checkPassword(it,confirmPassword.msg(),passwordDismatch)
            }
            confirmPassword.configAfterText {
                checkPassword(it,password.msg(),passwordDismatch)
            }
            selfIntroduction.configAfterText {
                val lengthText = "${it.length}${getString(R.string.text_length_postfix)}"
                selfIntoLength.text = lengthText
            }
            emailPostfix.config(EMAIL_PROFIX){_-> }
            birthdayYear.config(YEAR){selectValue->
                if (selectedMonth==2) updateDaySpinner(selectValue,2,birthdayDay)
                birthdayDay.setSelection(0)
            }
            birthdayMonth.config(MONTH){selectValue->
                selectedMonth=selectValue
                updateDaySpinner(birthdayYear.selectedItem as Int,selectValue,birthdayDay)
                birthdayDay.setSelection(0)
            }
            birthdayDay.config(DAY){_->}
            sex.config(SEX){_->}
            register.setOnClickListener {
                val dialog= SimpleDialog(this@AccountCreation).apply{
                    setTitle("提示")
                    setMessage("注册中，请稍后...")
                }
                val currentTime=System.currentTimeMillis()
                val userIdValue="${currentTime}${getRandomString(7)}"
                val createTimeValue=currentTime.toTime("yyyy-MM-dd HH:mm:ss")
                val emailValue=if(email.msg().isNotEmpty()) "${email.msg()}${EMAIL_PROFIX[emailPostfix.selectedItemPosition]}" else ""
                val phoneValue=phone.msg()
                val addressValue=address.msg()
                val selfIntroductionValue=selfIntroduction.msg()
                val isEdit=if((emailValue.isNotEmpty()) and (phoneValue.isNotEmpty()) and (addressValue.isNotEmpty()) and (selfIntroductionValue.isNotEmpty())) 1 else 0
                val ageValue=createTimeValue.subSequence(0,4).toString().toInt()-birthdayYear.selectedItem as Int
                val birthdayValue = "${birthdayYear.selectedItem as Int}-${birthdayMonth.selectedItem as Int}-${birthdayDay.selectedItem as Int}"
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val map1= User(userIdValue, nickname.msg(), name.msg(), ageValue, sex.selectedItem as String,password.msg(), createTimeValue,
                            0, 0, isEdit, emailValue, selfIntroductionValue, phoneValue, addressValue, birthdayValue, ""
                        ).POJO2Map()
                        map1["type"]="1"
                        val awaitResult = NetworkServerCreator.create<UserService>()
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
            Log.e("AccountCreation.pageInit", "${register.isClickable}")
        }
    }

    private fun updateDaySpinner(selectedYear:Int,seletedMonth:Int,spinner: Spinner){
        val arrayAdapter = spinner.adapter as ArrayAdapter<Int>
        arrayAdapter.clear()
        when (seletedMonth) {
            in intArrayOf(1,3,5,7,8,10,12) -> arrayAdapter.addAll(BIG_DAY)
            in intArrayOf(2) -> arrayAdapter.addAll(if (isLeapYear(selectedYear)) LEAP_DAY else COMMON_DAY)
            else -> arrayAdapter.addAll(SMALL_DAY)
        }
        arrayAdapter.notifyDataSetChanged()
    }

    private fun checkInformation(view1:View,view2:View){
        if (nameFlag and nicknameFlag and passwordFlag and passwordConfirmFlag){
            view1.isClickable=true
            view2.visibility=View.GONE
        }else{
            view1.isClickable=false
            view2.visibility=View.VISIBLE
        }
    }

    private fun TextView.configAfterText(afterText:TextView.(String)->Unit){
        this.addTextChangedListener {
            afterTextChanged { msg->
                afterText(msg.toString())
                checkInformation(binding.register,binding.registerHint)
            }
        }
    }

    private fun isLeapYear(year:Int)=when {
        year%100==0 -> year%400==0
        year%4==0 -> true
        else ->false
    }

    private fun checkPassword(ps1:String,ps2:String,layout: LinearLayout){
        if (ps1 == ps2){
            layout.visibility=View.GONE
            passwordConfirmFlag=true
        }else{
            layout.visibility= View.VISIBLE
            passwordConfirmFlag=false
        }
    }
}
