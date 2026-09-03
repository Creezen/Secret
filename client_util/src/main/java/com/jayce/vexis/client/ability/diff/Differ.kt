package com.jayce.vexis.client.ability.diff

abstract class Differ<T> : DiffCallback<T> {

    override fun onBlockStart(i: Int, size: Int) { /**/ }

    override fun onBlockEnd(i: Int) { /**/ }

    override fun onSnake(i: Int, startIndex: Int, destIndex: Int, item: T) { /**/ }

    override fun onAdd(i: Int, index: Int, destItem: T) { /**/ }

    override fun onDelete(i: Int, index: Int, startItem: T) { /**/ }
}