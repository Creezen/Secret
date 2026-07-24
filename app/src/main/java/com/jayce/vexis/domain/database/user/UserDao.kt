package com.jayce.vexis.domain.database.user

import androidx.room.Dao
import androidx.room.Insert
import com.jayce.vexis.domain.bean.UserEntry

@Dao
interface UserDao {

    @Insert
    fun insertUser(userEntry: UserEntry): Long
}