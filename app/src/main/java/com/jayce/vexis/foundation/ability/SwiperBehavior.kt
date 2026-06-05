package com.jayce.vexis.foundation.ability

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.jayce.vexis.R
import com.jayce.vexis.foundation.ui.block.swiper.HorizontalSwiperRecycle
import com.jayce.vexis.foundation.ui.block.swiper.ISwiper
import kotlin.math.abs

class SwiperBehavior(context: Context, attr: AttributeSet) : CoordinatorLayout.Behavior<View>(context, attr) {

    private var totalOffset: Int = 0
    private var triggerDistance: Int = 150
    private var willFling: Boolean = false
    private var rv: RecyclerView? = null
    private lateinit var swiper: ISwiper
    private lateinit var appbar: AppBarLayout
    private lateinit var swiperCallback: (() -> Unit)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        child.translationY = dependency.bottom.toFloat()
        return true
    }

    override fun onStartNestedScroll(
        layout: CoordinatorLayout,
        child: View,
        targetView: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        if (::swiperCallback.isInitialized.not()) return false
        if (swiper.isRefreshing) return false
        if (rv == null) {
            rv = child.findViewById(R.id.recycleView)
        }
        return true
    }

    override fun onNestedPreScroll(layout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(layout, child, target, dx, dy, consumed, type)
        totalOffset += dy
    }

    /**
     * 问题：
     *    当滑动停止的时候，需要确保回调只执行一次，且需要在最后一次滑动的时候执行
     *    否则会导致动画以及状态等混乱
     *
     * 当滑动的时候，有两种情况：
     *      1. 手动划了很长的距离，但是此时rv会继续惯性滑动（Fling）
     *      2. 手动划的距离比较短，rv不会惯性滑动
     *
     * 第一种情况，会调用onStopNestedScroll三次：
     *      手动滑动之前（totalOffset: 0  type: 0）
     *      手动滑动停止（totalOffset: -xxx  type: 0）
     *      惯性滑动停止（totalOffset: -xxx  type: 1）
     * 第二种情况，只会调用onStopNestedScroll两次：
     *      手动滑动之前（totalOffset: 0  type: 0）
     *      手动滑动停止（totalOffset: -xxx  type: 0）
     *
     * 两者的区别主要在于是否会执行惯性滑动
     * 而onStopNestedScroll方法，没有参数可以帮助判断在手动滑动之后，是否会执行惯性滑动
     * 但是通过日志发现，如果会惯性滑动，onNestedFling会执行，且在手动滑动停止之前
     * 所以可以加一个标志，在回调执行逻辑的情况下，
     *      如果这个标志为true，说明惯性滑动会执行，交由最后一次惯性滑动来执行回调逻辑
     *      否则的话，直接手动滑动结束之后就执行回调
     */
    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        if (totalOffset == 0 && type == ViewCompat.TYPE_TOUCH) willFling = false
        val canScrollDown = rv?.canScrollVertically(-1) ?: false
        val isRefresh = swiper.isRefreshing
        if (abs(totalOffset) > triggerDistance && !canScrollDown && !isRefresh) {
            if (willFling && type == ViewCompat.TYPE_TOUCH) return
            swiper.isRefreshing = true
            swiperCallback.invoke()
        } else {
            swiper.isRefreshing = false
        }
        totalOffset = 0
    }

    override fun onNestedFling(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        willFling = true
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)
    }

    fun setOnSwiperListener(listener: () -> Unit, swiper: ISwiper) {
        this.swiperCallback = listener
        this.swiper = swiper
        this.appbar = (swiper as HorizontalSwiperRecycle).binding.appbar
    }

    fun setTriggerDistance(distance: Int) {
        this.triggerDistance = distance
    }
}