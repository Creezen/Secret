package com.jayce.vexis.business.kit.maze.generator

import android.util.Log

class WilsonGenerator : IMazeGenerator() {

    override fun generateMaze(x: Int, y: Int) {
        Log.d("WilsonGenerator", "generateMaze")
    }
}