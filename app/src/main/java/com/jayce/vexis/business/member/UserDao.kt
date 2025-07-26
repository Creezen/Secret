package com.jayce.vexis.business.member

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    fun insertuser(user: User): Long

    @Query("select * from User where name = :name")
    fun fetchUser(name: String): List<User>
}
