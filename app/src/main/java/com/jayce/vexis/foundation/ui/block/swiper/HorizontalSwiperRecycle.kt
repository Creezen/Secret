package com.jayce.vexis.foundation.ui.block.swiper

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.databinding.HorizonSwiperRecycleLayoutBinding
import com.jayce.vexis.foundation.ability.SwiperBehavior

class HorizontalSwiperRecycle(context: Context, parent: ViewGroup) : ISwiper {

    override val binding: HorizonSwiperRecycleLayoutBinding
        = HorizonSwiperRecycleLayoutBinding.inflate(LayoutInflater.from(context), parent,true)

    override val payload: RecyclerView
        get() = binding.recycleView

    override var isRefreshing: Boolean = false
        set(value) {
            field = value
            if (!value) {
                payload.stopScroll()
                binding.appbar.setExpanded(false, true)
            }
        }

    private val behavior by lazy {
        val params = binding.recycleView.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior as SwiperBehavior
    }

    override fun setLoadingColors(vararg colors: Int) { /**/ }

    override fun setLoadingBackground(colorId: Int) {
        binding.loader.setBackgroundResource(colorId)
    }

    override fun setTriggerDistance(distance: Int) {
        behavior.setTriggerDistance(distance)
    }

    override fun setMaxOffset(offset: Int) { /**/ }

    override fun setOnRefreshListener(func: () -> Unit) {
        behavior.setOnSwiperListener(func, this)
    }
}