package com.jayce.vexis.member

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [UserItem::class], exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var userDatabase: UserDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): UserDatabase {
            userDatabase?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                UserDatabase::class.java,
                "app_database",
            ).build().apply {
                userDatabase = this
            }
        }
    }
}
