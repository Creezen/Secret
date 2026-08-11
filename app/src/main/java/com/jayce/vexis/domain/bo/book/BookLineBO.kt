package com.jayce.vexis.domain.bo.book

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookLineBO(val title: String, val time: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}