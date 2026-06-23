package com.jayce.vexis.domain.database.book

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jayce.vexis.domain.bean.book.BookLineEntry
import com.jayce.vexis.domain.bean.book.BookEntry

@Database(version = 1, entities = [BookLineEntry::class, BookEntry::class], exportSchema = false)
abstract class BookDatabase : RoomDatabase() {

    abstract fun recordDao(): RecordDao

    companion object {
        private var bookDatabase: BookDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): BookDatabase {
            bookDatabase?.let { return it }
            val databaseContext = context.applicationContext
            val cls = BookDatabase::class.java
            val name = "scoreDatabase"
            return Room.databaseBuilder(databaseContext, cls, name).build().apply { bookDatabase = this }
        }
    }
}