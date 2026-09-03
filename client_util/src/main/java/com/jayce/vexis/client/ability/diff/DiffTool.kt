package com.jayce.vexis.client.ability.diff

import com.jayce.vexis.client.DataTool.editScore
import kotlin.math.abs
import kotlin.math.pow

class DiffTool<T>(private val start: List<T>, private val dest: List<T>) {

    class Point(val x: Int, val y: Int)

    companion object {
        fun getMatchTrace(start: List<String>, dest: List<String>): List<Point> {
            if (start.isEmpty() || dest.isEmpty()) return listOf()
            val m = start.size
            val n = dest.size
            val scoreMatrix = Array(m) { Array(n) {DiffScore()} }
            val similar_0 = editScore(start[0], dest[0]).pow(2)
            val score_0 = scoreMatrix[0][0]
            score_0.from = P.SNAKE
            if (similar_0 >= 0.04) {
                score_0.score = similar_0
                score_0.add = true
            }
            for (i in 1 until m) {
                val score = scoreMatrix[i][0]
                score.from = P.DELETE
                val similar = editScore(start[i], dest[0]).pow(2)
                if (similar < 0.04) continue
                if (similar > scoreMatrix[i - 1][0].score) {
                    score.score = similar
                    score.add = true
                } else {
                    score.score = scoreMatrix[i - 1][0].score
                }
            }
            for (j in 1 until n) {
                val score = scoreMatrix[0][j]
                score.from = P.ADD
                val similar = editScore(start[0], dest[j]).pow(2)
                if (similar < 0.04) continue
                if (similar > scoreMatrix[0][j - 1].score) {
                    score.score = similar
                    score.add = true
                } else {
                    score.score = scoreMatrix[0][j - 1].score
                }
            }
            for (i in 1 until m)
                for (j in 1 until n) {
                    val similar = editScore(start[i], dest[j]).pow(2)
                    val score = scoreMatrix[i][j]
                    val left = scoreMatrix[i][j - 1]
                    val top = scoreMatrix[i - 1][j]
                    val snake = scoreMatrix[i - 1][j - 1]
                    if (left.score > top.score) {
                        score.from = P.ADD
                        score.score = left.score
                    } else {
                        score.from = P.DELETE
                        score.score = top.score
                    }
                    if (similar < 0.04) {
                        if (snake.score > score.score) {
                            score.score = snake.score
                            score.from = P.SNAKE
                        }
                    } else {
                        if (snake.score + similar > score.score) {
                            score.score = snake.score + similar
                            score.from = P.SNAKE
                            score.add = true
                        }
                    }
                }
            val trace = arrayListOf<Point>()
            var x = m - 1
            var y = n - 1
            var item: DiffScore
            while (x >= 0 && y >= 0) {
                item = scoreMatrix[x][y]
                if (item.add) {
                    trace.add(0, Point(x, y))
                    if ((x == 0 && y < n - 1) || (y == 0 && x < m -1)) return trace
                }
                when (item.from) {
                    P.ADD -> y--
                    P.DELETE -> x--
                    P.SNAKE -> {
                        x--
                        y--
                    }
                }
            }
            return trace
        }
    }

    private val m = start.size
    private val n = dest.size
    private val _blockTrace: List<List<Int>> = getDiffTrace()
    val trace: List<List<Int>> = _blockTrace

    fun showChange(callback: DiffCallback<T>) {
        var index1 = -1
        var index2 = -1
        _blockTrace.forEachIndexed { index, trace ->
            callback.onBlockStart(index, trace.size)
            trace.forEach {
                when (it) {
                    P.SNAKE -> {
                        index1++
                        index2++
                        callback.onSnake(index, index1, index2, start[index1])
                    }
                    P.ADD -> {
                        index2++
                        callback.onAdd(index, index2, dest[index2])
                    }
                    P.DELETE -> {
                        index1++
                        callback.onDelete(index, index1, start[index1])
                    }
                }
            }
            callback.onBlockEnd(index)
        }
    }

    private fun getDiffTrace(): List<List<Int>> {
        val diffList = diff(start, dest)
        val rawTrace = getTrace(diffList)
        return blockTrace(rawTrace)
    }

    private fun diff(start: List<T>, dest: List<T>): List<List<P>> {
        var hasFind = false
        val max = start.size + dest.size
        val vo = arrayListOf(P.zero)
        val historyV = arrayListOf(vo)
        if (moveStep(vo[0], P.zero, start, dest, vo)) return historyV
        for (d in 1 .. max) {
            val previewV = historyV.last()
            val v = arrayListOf<P>()
            find@ for (k in d downTo -d step 2) {
                for (item in previewV) {
                    if (abs(item.k - k) != 1) continue
                    if (moveStep(item, P.right, start, dest, v) ||
                        moveStep(item, P.down, start, dest, v)) {
                        hasFind = true
                        break@find
                    }
                }
            }
            historyV.add(v)
            if (hasFind) return historyV
        }
        return historyV
    }

    private fun moveStep(point: P, direction: P, start: List<T>, dest: List<T>, v: ArrayList<P>): Boolean {
        var dv = point + direction
        val m = start.size
        val n = dest.size
        while (dv.x < m && dv.y < n) {
            if (start[dv.x] == dest[dv.y])
                dv += P.snake
            else break
        }
        if (direction.operation > 0) dv.operation = direction.operation
        val existK = v.find { it.k == dv.k }
        if (existK == null || existK.x < dv.x) {
            v.remove(existK)
            v.add(dv)
        }
        if (dv.x == m && dv.y == n) return true
        return false
    }

    private fun getTrace(vHistory: List<List<P>>): List<Int> {
        val trace = arrayListOf<Int>()
        if (vHistory.size == 1) {
            repeat(vHistory[0][0].x) { trace.add(0, P.SNAKE) }
            return trace
        }
        var item = vHistory.last().find { it.x == m && it.y == n } ?: return trace
        var k = item.k
        for (i in (vHistory.size - 2) downTo 0 ) {
            val v = vHistory[i]
            when (item.operation) {
                P.ADD -> {
                    val p = v.find { it.k == k + 1 }
                    if (p != null) {
                        val snakeNum = item.x - p.x
                        repeat(snakeNum) { trace.add(0, P.SNAKE) }
                        trace.add(0, P.ADD)
                        item = p
                        k = p.k
                        continue
                    }
                }
                P.DELETE -> {
                    val p = v.find { it.k == k - 1 }
                    if (p != null) {
                        val snakeNum = item.y - p.y
                        repeat(snakeNum) { trace.add(0, P.SNAKE) }
                        trace.add(0, P.DELETE)
                        item = p
                        k = p.k
                        continue
                    }
                }
            }
        }
        val initVX = vHistory[0][0].x
        if (initVX > 0) repeat(initVX) { trace.add(0, P.SNAKE) }
        return trace
    }

    fun blockTrace(trace: List<Int>): List<List<Int>> {
        val list = arrayListOf<ArrayList<Int>>()
        var vBlock = arrayListOf<Int>()
        var lastType = trace[0]
        vBlock.add(trace[0])
        trace.forEachIndexed { index, v ->
            if (index == 0) return@forEachIndexed
            val isSameType = (v == lastType) || (v != P.SNAKE && lastType != P.SNAKE)
            lastType = v
            if (!isSameType){
                list.add(vBlock)
                vBlock = arrayListOf()
            }
            vBlock.add(v)
        }
        list.add(vBlock)
        return list
    }
}