package com.jayce.vexis.client.ability.diff

data class P(val x: Int, val y: Int, val k: Int) {

    companion object {
        const val SNAKE = 0
        const val DELETE = 1
        const val ADD = 2

        val right = P(1, 0, 1).apply { operation = DELETE }
        val down = P(0, 1, -1).apply { operation = ADD }
        val snake = P(1, 1, 0)
        val zero = P(0, 0, 0)
    }

    var operation: Int = -1

    operator fun plus(p: P): P {
        val k = x + p.x - (y + p.y)
        return P(x + p.x, y + p.y, k)
    }
}
