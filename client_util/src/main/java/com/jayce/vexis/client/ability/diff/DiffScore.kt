package com.jayce.vexis.client.ability.diff

class DiffScore(
    var score: Float = 0f,
    var add: Boolean = false,
    var from: Int = -1
) {

    override fun toString(): String {
        val str = "(score=$score add=$add from=$from)"
        return str
    }
}
