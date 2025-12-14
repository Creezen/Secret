package com.jayce.vexis.foundation.view.animator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jayce.vexis.foundation.bean.AnimatorBallEntry

class AnimatorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var ball: AnimatorBallEntry = AnimatorBallEntry(-1f, -1f)
    private val radius = 100f

    init {
        paint.color = Color.GREEN
    }

    override fun onDraw(canvas: Canvas) {
        if (ball.x > 0 && ball.y > 0) {
            canvas.drawCircle(ball.x, ball.y, radius, paint)
            return
        }
        ball.x = radius
        ball.y = height / 2f
        canvas.drawCircle(radius, height / 2f, radius, paint)
        val valueAnimator = setBallAnimator()
        val objectAnimator = setBackgroundAnimator()
        val animatorSet = AnimatorSet()
        animatorSet.play(valueAnimator).with(objectAnimator)
        animatorSet.setDuration(8000L)
        animatorSet.start()
    }

    private fun setBallAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofObject(
            BallEvaluator(),
            AnimatorBallEntry(radius, height / 2f),
            AnimatorBallEntry(width / 2f, height - radius),
            AnimatorBallEntry(width - radius, height / 2f),
            AnimatorBallEntry(width / 2f, radius),
            AnimatorBallEntry(radius, height / 2f)
        )
        valueAnimator.addUpdateListener {
            ball = it.animatedValue as AnimatorBallEntry
            invalidate()
        }
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        return valueAnimator
    }

    private fun setBackgroundAnimator(): ObjectAnimator {
        val objAnimator = ObjectAnimator.ofArgb(
            this,
            "backgroundColor",
            Color.parseColor("#00ffff"),
            Color.parseColor("#00ffff"),
            Color.parseColor("#0000ff"),
            Color.parseColor("#000000"),
            Color.parseColor("#ffffff"),
        )
        objAnimator.repeatCount = ValueAnimator.INFINITE
        return objAnimator
    }
}