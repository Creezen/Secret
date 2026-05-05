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

class SwiperBehavior(context: Context, attr: AttributeSet) : CoordinatorLayout.Behavior<View>(context, attr) {

    private var totalOffset: Int = 0
    private var triggerDistance: Int = 150
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

    override fun onStartNestedScroll(layout: CoordinatorLayout, child: View, targetView: View, target: View, axes: Int, type: Int): Boolean {
        if (::swiperCallback.isInitialized.not()) return false
        if (type != ViewCompat.TYPE_TOUCH) return false
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

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        val canScrollDown = rv?.canScrollVertically(-1) ?: false
        val isRefresh = swiper.isRefreshing
        if (Math.abs(totalOffset) > triggerDistance && !canScrollDown && !isRefresh) {
            appbar.setExpanded(true, true)
            swiper.isRefreshing = true
            swiperCallback.invoke()
        } else {
            swiper.isRefreshing = false
        }
        totalOffset = 0
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