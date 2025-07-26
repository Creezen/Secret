package com.jayce.vexis.business.kit.maze

interface MazeStatusCallback {
    fun onMove() {}

    fun onError() {}

    fun onFinish() {}
}
