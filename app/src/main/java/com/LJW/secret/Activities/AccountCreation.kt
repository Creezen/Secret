package com.LJW.secret.Activities

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.LJW.secret.Dialog.SimpleDialog
import com.LJW.secret.Network.UserService
import com.LJW.secret.NetworkServerCreator
import com.LJW.secret.POJO.User
import com.LJW.secret.POJO2Map
import com.LJW.secret.R
import com.LJW.secret.addTextChangedListener
import com.LJW.secret.await
import com.LJW.secret.databinding.CreateNewAccountBinding
import com.LJW.secret.getRandomString
import com.LJW.secret.msg
import com.LJW.secret.setOnItemSelectedListener
import com.LJW.secret.toTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AccountCreation : BaseActivity() {

    private var selectedMonth=1

    private var nameFlag = false
    private var nicknameFlag = false
    private var passwordFlag = false
    private var passwordConfirmFlag = false

    private val YEAR= (1920..2050).toList() as ArrayList
    private val MONTH= (1..12).toList() as ArrayList
    private val DAY=ArrayList<Int>()
    private val BIG_DAY= (1..31).toList() as ArrayList
    private val SMALL_DAY= (1..30).toList() as ArrayList
    private val LEAP_DAY= (1..29).toList() as ArrayList
    private val COMMON_DAY= (1..28).toList() as ArrayList
    private val SEX= arrayOf("男","女","保密").toList() as ArrayList<String>
    private lateinit var EMAIL_PROFIX:ArrayList<String>

    private lateinit var binding:CreateNewAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=CreateNewAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val accountName= intent.getStringExtra("intentAccount")
        if(!TextUtils.isEmpty(accountName)) binding.name.setText(accountName)
        EMAIL_PROFIX = resources.getStringArray(R.array.EmailProfix).toList() as ArrayList<String>
        pageInit()
    }

    fun pageInit(){
        with(binding){
            name.configAfterText {
                if(it.length in 6..18){
                    nameHintLength.visibility=View.GONE
                    var map=HashMap<String,String>()
                    map["type"] = "0"
                    map["name"] = name.msg()
                    GlobalScope.launch(Dispatchers.Main){
                        try {
                            var awaitResult = NetworkServerCreator.create<UserService>()
                                .checkUserName(map)
                                .await()
                            if (awaitResult["status"] == 0) {
                                name.setTextColor(Color.parseColor("#00ff00"))
                                nameHIntExist.setVisibility(View.GONE)
                                nameFlag = true
                            } else {
                                name.setTextColor(Color.parseColor("#000000"))
                                nameHIntExist.setVisibility(View.VISIBLE)
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
                if (it.length in 1..8){
                    this.setTextColor(Color.parseColor("#00ff00"))
                    nicknameFlag=true
                }else{
                    this.setTextColor(Color.parseColor("#000000"))
                    nicknameFlag=false
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
                selfIntoLength.setText("${it.length}/200")
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
                val dialog=SimpleDialog(this@AccountCreation).apply{
                    setTitle("提示")
                    setMessage("注册中，请稍后...")
                }
                val currentTime=System.currentTimeMillis()
                val userIdValue="${currentTime}${getRandomString(7)}"
                val createTimeValue=currentTime.toTime("yyyy-MM-dd HH:mm:ss")
                val emailValue=if(email.msg().length>0) "${email.msg()}${EMAIL_PROFIX[emailPostfix.selectedItemPosition]}" else ""
                val phoneValue=phone.msg()
                val addressValue=address.msg()
                val selfIntroductionValue=selfIntroduction.msg()
                val isEdit=if((emailValue.length>0) and (phoneValue.length>0) and (addressValue.length>0) and (selfIntroductionValue.length>0)) 1 else 0
                val ageValue=createTimeValue.subSequence(0,4).toString().toInt()-birthdayYear.selectedItem as Int
                val birthdayValue = "${birthdayYear.selectedItem as Int}-${birthdayMonth.selectedItem as Int}-${birthdayDay.selectedItem as Int}"
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        var map1=User(userIdValue, nickname.msg(), name.msg(), ageValue, sex.selectedItem as String,password.msg(), createTimeValue,
                            0, 0, isEdit, emailValue, selfIntroductionValue, phoneValue, addressValue, birthdayValue, ""
                        ).POJO2Map()
                        map1["type"]="1"
                        var awaitResult = NetworkServerCreator.create<UserService>()
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

    fun updateDaySpinner(selectedYear:Int,seletedMonth:Int,spinner: Spinner){
        val arrayAdapter = spinner.adapter as ArrayAdapter<Int>
        arrayAdapter.clear()
        if (seletedMonth in intArrayOf(1,3,5,7,8,10,12)) arrayAdapter.addAll(BIG_DAY)
        else if (seletedMonth==2) arrayAdapter.addAll(if (isLeapYear(selectedYear)) LEAP_DAY else COMMON_DAY)
        else arrayAdapter.addAll(SMALL_DAY)
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

    private fun <T> Spinner.config(list: ArrayList<T>,itemSelect:(T)->Unit){
        val spinnerAdapter=ArrayAdapter(applicationContext,R.layout.spinner_prompt,list)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown)
        this.adapter=spinnerAdapter
        this.setSelection(0)
        this.setOnItemSelectedListener {
            onItemSelected{_,_,pos,_ ->
                itemSelect(list[pos])
            }
        }
    }

    fun isLeapYear(year:Int)=when {
        year%100==0 -> year%400==0
        year%4==0 -> true
        else ->false
    }

    private fun checkPassword(ps1:String,ps2:String,layout: LinearLayout){
        if (ps1.equals(ps2)){
            layout.visibility=View.GONE
            passwordConfirmFlag=true
        }else{
            layout.visibility= View.VISIBLE
            passwordConfirmFlag=false
        }
    }
}
