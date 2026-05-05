package com.jayce.vexis.foundation.ui.block

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.jayce.vexis.R
import com.jayce.vexis.foundation.ui.block.swiper.ISwiper
import com.jayce.vexis.foundation.ui.block.swiper.SwiperFactory

class SwiperRecycleView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {

    private var swiper: ISwiper

    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.SwiperRecycleView).use {
            val type = it.getInt(R.styleable.SwiperRecycleView_refreshType, 0)
            swiper = SwiperFactory.get(context, this, SwiperFactory.SwiperType.of(type))
        }
    }

    private val payload: RecyclerView = swiper.payload

    var isRefreshing: Boolean
        get() = swiper.isRefreshing
        set(value) {
            swiper.isRefreshing = value
        }

    var adapter: RecyclerView.Adapter<*>
        get() = payload.adapter as RecyclerView.Adapter<*>
        set(value) {
            payload.adapter = value
        }

    var layoutManager: LayoutManager
        get() = payload.layoutManager as LayoutManager
        set(value) {
            payload.layoutManager = value
        }

    /**
     * 仅DEFAULT情况下有效
     */
    fun setLoadingColors(@ColorRes vararg colorRes: Int) {
        swiper.setLoadingColors(*colorRes)
    }

    fun setLoadingBackground(@ColorRes resId: Int) {
        swiper.setLoadingBackground(resId)
    }

    fun setTriggerDistance(distance: Int) {
        swiper.setTriggerDistance(distance)
    }

    /**
     * 仅DEFAULT情况下有效
     */
    fun setMaxOffset(offset: Int) {
        swiper.setMaxOffset(offset)
    }

    fun setOnRefreshListener(func: () -> Unit) {
        swiper.setOnRefreshListener(func)
    }
}