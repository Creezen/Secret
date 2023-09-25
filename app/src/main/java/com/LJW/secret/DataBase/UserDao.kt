package com.LJW.secret.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.LJW.secret.POJO.User

@Dao
interface UserDao {
    @Insert
    fun insertuser(user: User):Long

    @Query("select * from User where name = :name")
    fun fetchUser(name:String):List<User>
}