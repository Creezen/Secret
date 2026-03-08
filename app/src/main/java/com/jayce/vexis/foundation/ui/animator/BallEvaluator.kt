package com.jayce.vexis.foundation.ui.animator

import android.animation.TypeEvaluator
import com.jayce.vexis.domain.bean.AnimatorBallEntry

class BallEvaluator : TypeEvaluator<AnimatorBallEntry> {

    override fun evaluate(
        fraction: Float,
        start: AnimatorBallEntry?,
        end: AnimatorBallEntry?,
    ): AnimatorBallEntry {
        if (start == null || end == null) {
            return AnimatorBallEntry(0f, 0f)
        }
        val x = start.x + fraction * (end.x - start.x)
        val y = start.y + fraction * (end.y - start.y)
        return AnimatorBallEntry(x, y)
    }
}