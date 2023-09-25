package com.LJW.secret.Activities

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.LJW.secret.Network.UserService
import com.LJW.secret.NetworkServerCreator
import com.LJW.secret.POJO.User
import com.LJW.secret.R
import com.LJW.secret.addTextChangedListener
import com.LJW.secret.await
import com.LJW.secret.databinding.CreateNewAccountBinding
import com.LJW.secret.message
import com.LJW.secret.setOnItemSelectedListener
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
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
        EMAIL_PROFIX = resources.getStringArray(R.array.EmailProfix).toList() as ArrayList<String>
        pageInit()
    }

    fun pageInit(){
        with(binding){
            name.configAfterText {
                if(it.length in 6..18){
                    this.setTextColor(Color.parseColor("#000000"))
                    nameHintLength.visibility= View.GONE
                } else {
                    nameHIntExist.visibility=View.GONE
                    nameHintLength.visibility=View.VISIBLE
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
                checkPassword(it,confirmPassword.message(),passwordDismatch)
            }
            confirmPassword.configAfterText {
                checkPassword(it,password.message(),passwordDismatch)
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
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        Log.e("msg","gdrhfgh")
                        var toJson = JSON.toJSONString(
                            User("1", "fd", "123456", 26, "1", "2023",
                                10, 0, 0, "dfg", "th", "112", "ty", "hfg", "png"
                            )
                        )
                        var map1 = JSON.parseObject(toJson, HashMap::class.java) as HashMap<String,String>
                        map1["type"]="1"

                        Log.e("AccountCreation.pageInit", "${map1}")
                        var awaitResult = NetworkServerCreator.create<UserService>()
                            .checkUserName(map1)
                            .await()
                        Log.e("AccountCreation.pageInit", "${awaitResult["status"]}")
                    } catch (e: Exception) {
                        e.message?.let { it1 -> Log.e("msg", it1) }
                    }
                }

            }
            checkInformation(register,registerHint)

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
            view1.isClickable=true
            view2.visibility=View.VISIBLE
        }
    }

    private fun TextView.configAfterText(afterText:TextView.(String)->Unit){
        this.addTextChangedListener {
            afterTextChanged { msg->
                afterText(msg.toString())
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
