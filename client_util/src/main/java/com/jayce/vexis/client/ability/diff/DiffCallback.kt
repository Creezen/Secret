package com.jayce.vexis.client.ability.diff

interface DiffCallback <T> {

    fun onBlockStart(i: Int, size: Int)

    fun onDelete(i: Int, index: Int, startItem: T)

    fun onAdd(i: Int, index: Int, destItem: T)

    fun onSnake(i: Int, startIndex: Int, destIndex: Int, item: T)

    fun onBlockEnd(i: Int)
}