package com.jayce.vexis.domain.database.file

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jayce.vexis.util.Config.NIL
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class FileEntity(
    val userId: String,
    val fileName: String,
    val fileID: String,
    val fileSuffix: String,
    val description: String,
    val illustrate: String,
    val fileSize: Long,
    val uploadTime: String,
    val fileHash: String = NIL
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}