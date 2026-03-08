package com.jayce.vexis.domain.database.event

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jayce.vexis.domain.bean.EventEntry

@Database(version = 1, entities = [EventEntry::class], exportSchema = false)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        private var database: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            return database ?: run {
                Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "EventDatabase"
                ).build().apply {
                    database = this
                }
            }
        }
    }
}