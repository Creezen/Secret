package com.jayce.vexis.stylized.animator

import android.animation.TypeEvaluator
import com.jayce.vexis.stylized.animator.AnimatorBall

class BallEvaluator: TypeEvaluator<AnimatorBall> {
    override fun evaluate(fraction: Float, start: AnimatorBall?, end: AnimatorBall?): AnimatorBall {
        if (start == null || end == null) {
            return AnimatorBall(0f, 0f)
        }
        val x = start.x + fraction * (end.x - start.x)
        val y = start.y + fraction * (end.y - start.y)
        return AnimatorBall(x, y)
    }

}