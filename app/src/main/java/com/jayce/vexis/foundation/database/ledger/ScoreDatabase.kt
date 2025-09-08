package com.jayce.vexis.foundation.database.ledger

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jayce.vexis.foundation.bean.RecordItemEntry
import com.jayce.vexis.foundation.bean.ScoreEntry

@Database(version = 1, entities = [RecordItemEntry::class, ScoreEntry::class], exportSchema = false)
abstract class ScoreDatabase : RoomDatabase() {

    abstract fun recordDao(): RecordDao

    companion object {
        private var scoreDatabase: ScoreDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): ScoreDatabase {
            scoreDatabase?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext, ScoreDatabase::class.java, "scoreDatabase")
                .build()
                .apply {
                    scoreDatabase = this
                }
        }
    }
}