package com.jayce.vexis.domain.viewmodel

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.util.bean.TransferStatusBean
import com.jayce.vexis.util.bean.UserBean
import com.jayce.vexis.util.getRandomString
import com.jayce.vexis.util.toTime
import com.jayce.vexis.client.AndroidTool.getString
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.domain.bean.TimeUnitEntry
import com.jayce.vexis.domain.route.UserService
import com.jayce.vexis.foundation.Util.request

class RegisterViewModel : BaseViewModel() {

    private var isPasswordValid: Boolean = false
    private var isConfirmPasswordValid: Boolean = false

    val roleId = MutableLiveData<String>()
    val showRoleIdIcon = MutableLiveData(false)
    val roleIdHint = MutableLiveData(NIL)
    val isRoleIdValid = MutableLiveData(false)

    val nickname = MutableLiveData<String>()
    val showNicknameIcon = MutableLiveData(false)
    val nickNameHint = MutableLiveData(NIL)
    val isNicknameValid = MutableLiveData(false)

    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    val showPasswordIcon = MutableLiveData(false)
    val showConfirmPasswordIcon = MutableLiveData(false)
    val passwordHint = MutableLiveData(NIL)
    val confirmPasswordHint = MutableLiveData(NIL)

    val birthdayYear = MutableLiveData("2000")
    val birthdayMonth = MutableLiveData("1")
    val birthdayDay = MutableLiveData("1")

    val sexSelectPosition = MutableLiveData(2)
    val emailContent = MutableLiveData(NIL)
    val emailPostfixSelectPosition = MutableLiveData(0)
    val phoneNumber = MutableLiveData<String>()
    val address = MutableLiveData<String>()

    val bio = MutableLiveData<String>()
    val showBioIcon = MutableLiveData(false)
    val bioHint = MutableLiveData(NIL)

    val isRegisterButtonClickable = MutableLiveData(false)

    val sexList = arrayOf("男", "女", "保密").toList() as ArrayList<String>
    val emailSuffix: ArrayList<String> = arrayListOf()

    fun initStatus(resources: Resources, initRoleId: String?) {
        if (!initRoleId.isNullOrEmpty()) {
            roleId.value = initRoleId ?: NIL
        }
        emailSuffix.addAll(resources.getStringArray(R.array.EmailProfix).toList())
    }

    fun handleRoleId(string: String) {
        val isValid = string.length in 6..18
        if (!isValid) {
            showRoleIdIcon.value = true
            roleIdHint.value = getString(R.string.from_6_to_18)
            isRoleIdValid.value = false
            checkRegisterButtonStatus()
        } else {
            showRoleIdIcon.value = false
            request<UserService, Boolean>({ checkInfo(string) }) { status ->
                showRoleIdIcon.value = status.not()
                isRoleIdValid.value = status
                if (!status) {
                    roleIdHint.value = getString(R.string.role_id_exist)
                }
                checkRegisterButtonStatus()
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
        val isLengthValid = nickname.length in 1..8
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

    fun handleBirthday(timeUnitEntry: TimeUnitEntry) {
        birthdayYear.value = timeUnitEntry.year.toString()
        birthdayMonth.value = timeUnitEntry.month.toString()
        birthdayDay.value = timeUnitEntry.day.toString()
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
        val userId = getRandomString(10)
        val createTime = currentTime.toTime("yyyy-MM-dd HH:mm:ss")
        val nicknameValue = nickname.value ?: NIL
        val roleIdValue = roleId.value ?: NIL
        val sexValue = sexList[sexSelectPosition.value ?: 0]
        val passwordValue = password.value ?: NIL
        val emailSuffixValue = emailSuffix[emailPostfixSelectPosition.value ?: 0]
        val emailValue = if (emailContent.value.isNullOrEmpty()) {
            NIL
        } else {
            "${emailContent.value}$emailSuffixValue"
        }
        val phoneNum = phoneNumber.value ?: NIL
        val addressValue = address.value ?: NIL
        val bioValue = bio.value ?: NIL
        val isEdit = if (isUserProfileEdit(emailValue, phoneNum, addressValue, bioValue)) 1 else 0
        val age = createTime.substring(0, 4).toInt() - (birthdayYear.value?.toInt() ?: 2025)
        val birthday = "${birthdayYear.value}-${birthdayMonth.value}-${birthdayDay.value}"
        val bean = UserBean(
            userId, nicknameValue, roleIdValue, age, sexValue, passwordValue, createTime,
            0, 0, 0, isEdit, emailValue, bioValue, phoneNum, addressValue, birthday, NIL
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
        return emailValue.isNotEmpty() &&
            phoneNum.isNotEmpty() &&
            addressValue.isNotEmpty() &&
            bioValue.isNotEmpty()
    }

    private fun checkRegisterButtonStatus() {
        isRegisterButtonClickable.value =
            isRoleIdValid.value ?: false &&
            isNicknameValid.value ?: false &&
            isPasswordValid &&
            isConfirmPasswordValid
    }
}