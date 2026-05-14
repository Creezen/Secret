package com.jayce.vexis.business.kit.maze

import com.jayce.vexis.business.kit.maze.generator.DFSGenerator
import com.jayce.vexis.business.kit.maze.generator.GrowTreeGenerator
import com.jayce.vexis.business.kit.maze.generator.IMazeGenerator
import com.jayce.vexis.business.kit.maze.generator.WilsonGenerator
import com.jayce.vexis.domain.enums.MazeType

object MazeFactory {

    fun getGenerator(type: MazeType): IMazeGenerator {
        return when (type) {
            MazeType.DFS -> DFSGenerator()
            MazeType.WILSON -> WilsonGenerator()
            MazeType.GROW_TREE -> GrowTreeGenerator()
        }
    }
}