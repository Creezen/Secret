package com.jayce.vexis.utility.maze

interface MazeStatusCallback {

    fun onMove(){}

    fun onError(){}

    fun onFinish(){}
}