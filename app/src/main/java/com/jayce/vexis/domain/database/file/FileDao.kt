package com.jayce.vexis.domain.database.file

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jayce.vexis.domain.bean.FileEntry

@Dao
interface FileDao {
    @Insert
    fun insertFileRecord(fileEntry: FileEntry)

    @Query("select * from FileEntry where fileHash = :hash")
    fun queryItemByHash(hash: String): FileEntry?
}