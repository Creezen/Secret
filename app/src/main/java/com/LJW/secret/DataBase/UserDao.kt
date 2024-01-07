package com.ljw.secret.dataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ljw.secret.pojo.User

@Dao
interface UserDao {
    @Insert
    fun insertuser(user: User):Long

    @Query("select * from User where name = :name")
    fun fetchUser(name:String):List<User>
}