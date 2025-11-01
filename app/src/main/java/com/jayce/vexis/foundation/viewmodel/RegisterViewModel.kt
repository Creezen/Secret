package com.jayce.vexis.foundation.viewmodel

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import com.creezen.commontool.Config.Constant.EMPTY_STRING
import com.creezen.commontool.Config.Constant.NUM_7
import com.creezen.commontool.bean.TransferStatusBean
import com.creezen.commontool.bean.UserBean
import com.creezen.commontool.getRandomString
import com.creezen.commontool.toTime
import com.creezen.tool.AndroidTool.getString
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.route.UserService

class RegisterViewModel : BaseViewModel() {

    private var isPasswordValid: Boolean = false
    private var isConfirmPasswordValid: Boolean = false

    val roleId = MutableLiveData<String>()
    val showRoleIdIcon = MutableLiveData(false)
    val roleIdHint = MutableLiveData(EMPTY_STRING)
    val isRoleIdValid = MutableLiveData(false)

    val nickname = MutableLiveData<String>()
    val showNicknameIcon = MutableLiveData(false)
    val nickNameHint = MutableLiveData(EMPTY_STRING)
    val isNicknameValid = MutableLiveData(false)

    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    val showPasswordIcon = MutableLiveData(false)
    val showConfirmPasswordIcon = MutableLiveData(false)
    val passwordHint = MutableLiveData(EMPTY_STRING)
    val confirmPasswordHint = MutableLiveData(EMPTY_STRING)

    val birthdayYear = MutableLiveData("2000")
    val birthdayMonth = MutableLiveData("1")
    val birthdayDay = MutableLiveData("1")

    val sexSelectPosition = MutableLiveData(2)
    val emailContent = MutableLiveData(EMPTY_STRING)
    val emailPostfixSelectPosition = MutableLiveData(0)
    val phoneNumber = MutableLiveData<String>()
    val address = MutableLiveData<String>()

    val bio = MutableLiveData<String>()
    val showBioIcon = MutableLiveData(false)
    val bioHint = MutableLiveData(EMPTY_STRING)

    val isRegisterButtonClickable = MutableLiveData(false)

    val sexList = arrayOf("男", "女", "保密").toList() as ArrayList<String>
    val emailSuffix: ArrayList<String> = arrayListOf()

    fun initStatus(resources: Resources, initRoleId: String?) {
        if (!initRoleId.isNullOrEmpty()) {
            roleId.value = initRoleId ?: EMPTY_STRING
        }
        emailSuffix.addAll(resources.getStringArray(R.array.EmailProfix).toList())
    }

    fun handleRoleId(string: String) {
        val isValid = string.length in 6 .. 18
        if (!isValid) {
            showRoleIdIcon.value = true
            roleIdHint.value = getString(R.string.from_6_to_18)
            isRoleIdValid.value = false
            checkRegisterButtonStatus()
        } else {
            showRoleIdIcon.value = false
            request<UserService, Boolean>({ checkInfo(string) }){ status ->
                ui {
                    showRoleIdIcon.value = status.not()
                    isRoleIdValid.value = status
                    if (!status) {
                        roleIdHint.value = getString(R.string.role_id_exist)
                    }
                    checkRegisterButtonStatus()
                }
            }
        }
    }

    fun handleNickName(nickname: String) {
        if (nickname.contains(" ")) {
            isNicknameValid.value = false
            showNicknameIcon.value = true
            nickNameHint.value = getString(R.string.nickname_no_blank)
            checkRegisterButtonStatus()
            return
        }
        val isLengthValid = nickname.length in 1 .. 8
        isNicknameValid.value = isLengthValid
        showNicknameIcon.value = isLengthValid.not()
        if (!isLengthValid) {
            nickNameHint.value = getString(R.string.nickname_hint)
        }
        checkRegisterButtonStatus()
    }

    fun handlePassword(string: String) {
        val isValid = string.length in 6..18
        showPasswordIcon.value = isValid.not()
        isPasswordValid = isValid
        if (!isValid) {
            passwordHint.value = getString(R.string.password_length_6_18)
        }
        checkRegisterButtonStatus()
    }

    fun handleConfirmPassword(string: String) {
        val isValid = password.value.equals(confirmPassword.value)
        showConfirmPasswordIcon.value = isValid.not()
        isConfirmPasswordValid = isValid
        if (!isValid) {
            confirmPasswordHint.value = getString(R.string.password_not_equal)
        }
        checkRegisterButtonStatus()
    }

    fun handleBirthday(list: List<String>) {
        birthdayYear.value = list[0]
        birthdayMonth.value = list[1]
        birthdayDay.value = list[2]
    }

    fun handleBio(string: String) {
        showBioIcon.value = string.isEmpty().not()
        bioHint.value = if (string.length > 200) {
            getString(R.string.max_bio_length)
        } else {
            getString(R.string.bio_length, string.length)
        }
    }

    fun registerRole(callback: (Boolean) -> Unit) {
        val currentTime = System.currentTimeMillis()
        val userId = "${currentTime}${getRandomString(NUM_7)}"
        val createTime = currentTime.toTime("yyyy-MM-dd HH:mm:ss")
        val nicknameValue = nickname.value ?: EMPTY_STRING
        val roleIdValue = roleId.value ?: EMPTY_STRING
        val sexValue = sexList[sexSelectPosition.value ?: 0]
        val passwordValue = password.value ?: EMPTY_STRING
        val emailSuffixValue = emailSuffix[emailPostfixSelectPosition.value ?: 0]
        val emailValue = if (emailContent.value.isNullOrEmpty()) {
            EMPTY_STRING
        } else {
            "${emailContent.value}${emailSuffixValue}"
        }
        val phoneNum = phoneNumber.value ?: EMPTY_STRING
        val addressValue = address.value ?: EMPTY_STRING
        val bioValue = bio.value ?: EMPTY_STRING
        val isEdit = if (isUserProfileEdit(emailValue, phoneNum, addressValue, bioValue)) 1 else 0
        val age = createTime.substring(0, 4).toInt() - (birthdayYear.value?.toInt() ?: 2025)
        val birthday = "${birthdayYear.value}-${birthdayMonth.value}-${birthdayDay.value}"
        val bean = UserBean(
            userId, nicknameValue, roleIdValue, age, sexValue, passwordValue, createTime,
            0, 0, isEdit, emailValue, bioValue, phoneNum, addressValue, birthday, EMPTY_STRING
        )
        request<UserService, TransferStatusBean>({ register(bean) }) {
            callback.invoke(it.statusCode == 2)
        }
    }

    private fun isUserProfileEdit(
        emailValue: String,
        phoneNum: String,
        addressValue: String,
        bioValue: String
    ): Boolean {
        return emailValue.isNotEmpty()
                && phoneNum.isNotEmpty()
                && addressValue.isNotEmpty()
                && bioValue.isNotEmpty()
    }

    private fun checkRegisterButtonStatus() {
        isRegisterButtonClickable.value =
            isRoleIdValid.value ?: false
            && isNicknameValid.value ?: false
            && isPasswordValid
            && isConfirmPasswordValid
    }
}