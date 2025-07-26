package com.jayce.vexis.business.history

data class TraceCell (
    var minX: Float = 0f,
    var maxX: Float = 0f,
    var minY: Float = 0f,
    var maxY: Float = 0f,
    val percent: Float,
    val timeStamp: Long,
    val message: String = ""
) {
    fun isClicked(x: Float, y: Float): Boolean {
        return x in minX..maxX && y in minY.. maxY
    }
}