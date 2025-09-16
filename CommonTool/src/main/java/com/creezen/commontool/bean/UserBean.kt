package com.creezen.commontool.bean

data class UserBean(
    val userId: String = "-1",
    val nickname: String = "NickUser",
    val name: String = "匿名用户",
    val age: Int = -1,
    val sex: String = "Unknow",
    val password: String = "",
    val createTime: String = "",
    val count: Long = -1,
    val administrator: Int = -1,
    val isEdit: Int = -1,
    val email: String = "",
    val selfIntroduction: String = "",
    val phone: String = "",
    val address: String = "",
    val birthday: String = "",
    val headType: String = "",
) {
    fun isAdministrator(): Boolean {
        return administrator == 1
    }
}
