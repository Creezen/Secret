package com.jayce.vexis.domain.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jayce.vexis.util.Config.NIL

@Entity(tableName = "eventEntry")
data class EventEntry(
    val type: Int,
    val userId: String,
    val nickName: String,
    val session: String,
    val time: Long,
    val isRead: Boolean = false,
    val msgId: String = "-1",
    val content: String = NIL
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}