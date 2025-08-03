package com.jayce.vexis.foundation.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class UserEntry(
    var userId: String = "-1",
    var nickname: String = "NickUser",
    var name: String = "匿名用户",
    var age: Int = -1,
    var sex: String = "Unknow",
    var password: String = "",
    var createTime: String = "",
    var count: Long = -1,
    var administrator: Int = -1,
    var isEdit: Int = -1,
    var email: String = "",
    var selfIntroduction: String = "",
    var phone: String = "",
    var address: String = "",
    var birthday: String = "",
    var headType: String = "",
) : Parcelable {
    @PrimaryKey
    var id: Long = 0

    fun isAdministrator(): Boolean {
        return administrator == 1
    }
}