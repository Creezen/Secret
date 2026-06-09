package com.jayce.vexis.domain.database.ledger

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jayce.vexis.domain.bean.BookLineEntry
import com.jayce.vexis.domain.bean.BookEntry

@Database(version = 1, entities = [BookLineEntry::class, BookEntry::class], exportSchema = false)
abstract class BookDatabase : RoomDatabase() {

    abstract fun recordDao(): RecordDao

    companion object {
        private var bookDatabase: BookDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): BookDatabase {
            bookDatabase?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext, BookDatabase::class.java, "scoreDatabase")
                .build()
                .apply {
                    bookDatabase = this
                }
        }
    }
}