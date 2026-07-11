package com.jayce.vexis.domain.database.file

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jayce.vexis.domain.bean.FileEntry

@Database(version = 1, entities = [FileEntry::class], exportSchema = false)
abstract class FileDatabase : RoomDatabase(){

    abstract fun fileDao(): FileDao

    companion object {
        private var database: FileDatabase? = null

        @Synchronized
        fun getDatabse(context: Context): FileDatabase {
            database?.let { return it }
            val roomContext = context.applicationContext
            val cls = FileDatabase::class.java
            val name = "FileDatabase"
            val fileDatabase = Room.databaseBuilder(roomContext, cls, name).build()
            database = fileDatabase
            return fileDatabase
        }
    }
}