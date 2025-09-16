package com.jayce.vexis.foundation.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jayce.vexis.foundation.bean.UserEntry

@Dao
interface UserDao {

    @Insert
    fun insertUser(userEntry: UserEntry): Long

    @Query("select * from UserEntry where name = :name")
    fun fetchUser(name: String): List<UserEntry>
}