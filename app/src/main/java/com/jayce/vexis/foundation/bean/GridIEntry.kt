package com.jayce.vexis.foundation.bean

data class GridIEntry(
    var x: Int = 0,
    var y: Int = 0,
    var visit: Boolean = false,
    var left: Boolean = true,
    var top: Boolean = true,
    var right: Boolean = true,
    var bottom: Boolean = true,
)