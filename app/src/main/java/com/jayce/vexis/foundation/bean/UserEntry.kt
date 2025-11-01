package com.jayce.vexis.foundation.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.creezen.commontool.Config.Constant.EMPTY_STRING
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class UserEntry(
    val userId: String = "-1",
    val nickname: String = "NickUser",
    val name: String = "匿名用户",
    val age: Int = -1,
    val sex: String = "Unknow",
    val password: String = EMPTY_STRING,
    val createTime: String = EMPTY_STRING,
    val count: Long = -1,
    val administrator: Int = -1,
    val isEdit: Int = -1,
    val email: String = EMPTY_STRING,
    val selfIntroduction: String = EMPTY_STRING,
    val phone: String = EMPTY_STRING,
    val address: String = EMPTY_STRING,
    val birthday: String = EMPTY_STRING,
    val headType: String = EMPTY_STRING,
) : Parcelable {

    @PrimaryKey
    var id: Long = 0
}