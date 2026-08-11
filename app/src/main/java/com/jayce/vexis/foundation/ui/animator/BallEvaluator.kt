package com.jayce.vexis.foundation.ui.animator

import android.animation.TypeEvaluator
import com.jayce.vexis.domain.bo.BallBO

class BallEvaluator : TypeEvaluator<BallBO> {

    override fun evaluate(
        fraction: Float,
        start: BallBO?,
        end: BallBO?
    ): BallBO {
        if (start == null || end == null) {
            return BallBO(0f, 0f)
        }
        val x = start.x + fraction * (end.x - start.x)
        val y = start.y + fraction * (end.y - start.y)
        return BallBO(x, y)
    }
}