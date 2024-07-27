package com.jayce.vexis.member

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class UserItem(
    var userId:String,
    var nickname:String,
    var name:String,
    var age:Int,
    var sex:String,
    var password:String,
    var createTime:String,
    var count:Long,
    var administrator:Int,
    var isEdit:Int,
    var email:String,
    var selfIntroduction:String,
    var phone:String,
    var address: String,
    var birthday:String,
    var headType:String
):Parcelable{
    @PrimaryKey
    var id:Long=0
}
