package com.ljw.secret.dataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ljw.secret.bean.UserItem

@Dao
interface UserDao {
    @Insert
    fun insertuser(userItem: UserItem):Long

    @Query("select * from UserItem where name = :name")
    fun fetchUser(name:String):List<UserItem>
}