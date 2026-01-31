package com.jayce.vexis.foundation.ui.animator

import android.content.Context
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.R

class RecycleItemAnimator(val context: Context) : DefaultItemAnimator() {

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        if (holder == null) return true
        val animator = AnimationUtils.loadAnimation(context, R.anim.recycle_anim)
        animator.startOffset = holder.layoutPosition * 50L
        dispatchAddStarting(holder)
        animator.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) { /**/ }

            override fun onAnimationEnd(animation: Animation?) {
                dispatchAddFinished(holder)
            }

            override fun onAnimationRepeat(animation: Animation?) { /**/ }
        })
        holder.itemView.startAnimation(animator)
        return true
    }
}