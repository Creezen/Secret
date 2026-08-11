package com.jayce.vexis.domain.bo

data class GridBO(
    var x: Int = 0,
    var y: Int = 0,
    var visit: Boolean = false,
    var hasLeft: Boolean = true,
    var hasTop: Boolean = true,
    var hasRight: Boolean = true,
    var hasBottom: Boolean = true,
)