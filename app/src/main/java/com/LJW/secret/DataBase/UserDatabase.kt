package com.LJW.secret.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.LJW.secret.POJO.User

@Database(version = 1, entities = [User::class])
abstract class UserDatabase:RoomDatabase() {
    abstract fun userDao(): UserDao
    companion object {
        private var userDatabase: UserDatabase? = null
        @Synchronized
        fun getDatabase(context: Context): UserDatabase {
            userDatabase?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                UserDatabase::class.java, "app_database")
                .build().apply {
                    userDatabase = this
                }
        }
    }
}