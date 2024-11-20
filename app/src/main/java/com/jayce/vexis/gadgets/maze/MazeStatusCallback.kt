package com.jayce.vexis.gadgets.maze

interface MazeStatusCallback {

    fun onMove(){}

    fun onError(){}

    fun onFinish(){}
}