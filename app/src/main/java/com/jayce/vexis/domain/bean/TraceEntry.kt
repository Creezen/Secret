package com.jayce.vexis.domain.bean

import com.creezen.commontool.Config.NIL

data class TraceEntry(
    var minX: Float = 0f,
    var maxX: Float = 0f,
    var minY: Float = 0f,
    var maxY: Float = 0f,
    val percent: Float,
    val timeStamp: Long,
    val message: String = NIL
) {
    fun isClicked(x: Float, y: Float): Boolean {
        return x in minX..maxX && y in minY..maxY
    }
}