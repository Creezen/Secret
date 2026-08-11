package com.jayce.vexis.domain.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jayce.vexis.R
import com.jayce.vexis.client.AndroidTool.getString
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.domain.bo.TimeBO
import com.jayce.vexis.domain.route.UserService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.util.dto.AuthDTO
import com.jayce.vexis.util.dto.PrivilegeDTO
import com.jayce.vexis.util.dto.ProfileDTO
import com.jayce.vexis.util.dto.UserDTO
import com.jayce.vexis.util.getRandomString
import com.jayce.vexis.util.toTime
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RegisterViewModel : BaseViewModel() {

    private var isPasswordValid: Boolean = false
    private var isConfirmPasswordValid: Boolean = false

    private val _emailFlow: MutableSharedFlow<Pair<String, String>> =
        MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)
    val emailFlow = _emailFlow.asSharedFlow()

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
    val emailType = MutableLiveData(NIL)
    val isEmailValid = MutableLiveData(false)
    val phoneNumber = MutableLiveData<String>()
    val address = MutableLiveData<String>()

    val bio = MutableLiveData<String>()
    val showBioIcon = MutableLiveData(false)
    val bioHint = MutableLiveData(NIL)

    val isRegisterButtonClickable = MutableLiveData(false)

    val sexList = arrayOf("男", "女", "保密").toList() as ArrayList<String>

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

    fun handleEmailContent(email: String) {
        isEmailValid.value = email.length >= 5
        checkRegisterButtonStatus()
    }

    fun handleBirthday(timeBO: TimeBO) {
        birthdayYear.value = timeBO.year.toString()
        birthdayMonth.value = timeBO.month.toString()
        birthdayDay.value = timeBO.day.toString()
    }

    fun handleBio(string: String) {
        showBioIcon.value = string.isEmpty().not()
        bioHint.value = if (string.length > 200) {
            getString(R.string.max_bio_length)
        } else {
            getString(R.string.bio_length, string.length)
        }
    }

    fun sendEmail() {
        val userId = getRandomString(10)
        val emailTypeValue = emailType.value
        val email = "${emailContent.value}$emailTypeValue"
        request<UserService, _>({ sendEmailCode(userId, email) }) {
            if (it.statusCode != 0) it.data.toast()
            else _emailFlow.emit(userId to email)
        }
    }

    fun registerRole(userId: String, email: String, code: String, onResult: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        val createTime = currentTime.toTime("yyyy-MM-dd HH:mm:ss")
        val nicknameValue = nickname.value ?: NIL
        val sexValue = sexList[sexSelectPosition.value ?: 0]
        val passwordValue = password.value ?: NIL
        val phoneNum = phoneNumber.value ?: NIL
        val addressValue = address.value ?: NIL
        val bioValue = bio.value ?: NIL
        val isEdit = if (isUserProfileEdit(phoneNum, addressValue, bioValue)) 1 else 0
        val age = createTime.substring(0, 4).toInt() - (birthdayYear.value?.toInt() ?: 2025)
        val birthday = "${birthdayYear.value}-${birthdayMonth.value}-${birthdayDay.value}"
        val auth = AuthDTO(passwordValue, "")
        val profile = ProfileDTO(nicknameValue, age, sexValue, email, bioValue, phoneNum, addressValue, birthday, NIL, isEdit)
        val privilege = PrivilegeDTO(0, 0, 0)
        val bean = UserDTO(userId, createTime, auth, profile, privilege)
        request<UserService, _>({ register(bean, code) }) {
            if (it.statusCode != 0) it.data.toast()
            else onResult.invoke()
        }
    }

    private fun isUserProfileEdit(
        phoneNum: String,
        addressValue: String,
        bioValue: String
    ): Boolean {
        return phoneNum.isNotEmpty() &&
            addressValue.isNotEmpty() &&
            bioValue.isNotEmpty()
    }

    private fun checkRegisterButtonStatus() {
        isRegisterButtonClickable.value =
            isNicknameValid.value ?: false &&
            isEmailValid.value ?: false &&
            isPasswordValid &&
            isConfirmPasswordValid
    }
}