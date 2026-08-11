package com.jayce.vexis.domain.database.user

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface UserDao {

    @Insert
    fun insertUser(userEntity: UserEntity): Long
}