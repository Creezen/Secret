package com.jayce.vexis.domain.database.file

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FileDao {
    @Insert
    fun insertFileRecord(fileEntity: FileEntity)

    @Query("select * from FileEntity where fileHash = :hash")
    fun queryItemByHash(hash: String): FileEntity?
}