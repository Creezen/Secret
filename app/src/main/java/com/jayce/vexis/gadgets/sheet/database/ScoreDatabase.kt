package com.jayce.vexis.gadgets.sheet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jayce.vexis.gadgets.sheet.bean.RecordItemBean
import com.jayce.vexis.gadgets.sheet.bean.ScoreBean

@Database(version = 1, entities = [RecordItemBean::class, ScoreBean::class], exportSchema = false)
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
                .build().apply {
                    scoreDatabase = this
                }
        }
    }
}