package com.jayce.vexis.domain.bean.book

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookLineEntry(val title: String, val time: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}