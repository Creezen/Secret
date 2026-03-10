package com.jayce.vexis.domain.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.creezen.commontool.Config.Constant.EMPTY_STRING

@Entity(tableName = "eventEntry")
data class EventEntry (
    val type: Int,
    val userId: String,
    val nickName: String,
    val session: String,
    val msgId: String = "-1",
    val content: String = EMPTY_STRING
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}