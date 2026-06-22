package com.jayce.vexis.domain.bean

import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.util.toTime

data class MomentEntry(
    val timeStamp: Long,
    val message: String = NIL
) {
    private var minX: Float = 0f
    private var maxX: Float = 0f
    private var minY: Float = 0f
    private var maxY: Float = 0f
    var percent: Float = 0f

    fun isClicked(x: Float, y: Float): Boolean {
        return x in minX..maxX && y in minY..maxY
    }

    fun updateRect(minX: Float, maxX: Float, minY: Float, maxY: Float) {
        this.minX = minX
        this.minY = minY
        this.maxX = maxX
        this.maxY = maxY
    }

    override fun toString(): String {
        return "${timeStamp.toTime()} - $minX - $minY - $maxX - $maxY"
    }
}