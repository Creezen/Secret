package com.jayce.vexis.domain.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.creezen.commontool.Config.NIL
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class UserEntry(
    val userId: String = "-1",
    val nickname: String = "NickUser",
    val name: String = "匿名用户",
    val age: Int = -1,
    val sex: String = "Unknow",
    val password: String = NIL,
    val createTime: String = NIL,
    val count: Long = -1,
    val level: Int = 0,
    val adminLevel: Int = 0,
    val isEdit: Int = -1,
    val email: String = NIL,
    val selfIntroduction: String = NIL,
    val phone: String = NIL,
    val address: String = NIL,
    val birthday: String = NIL,
    val headType: String = NIL,
) : Parcelable {

    @PrimaryKey
    var id: Long = 0
}