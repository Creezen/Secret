package com.jayce.vexis.foundation.ui.block.swiper

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.databinding.DefaultSwiperRecycleLayoutBinding

class DefaultSwiperRecycle(context: Context, parent: ViewGroup) : ISwiper {

    override val binding: DefaultSwiperRecycleLayoutBinding =
        DefaultSwiperRecycleLayoutBinding.inflate(LayoutInflater.from(context), parent, true)

    override val payload: RecyclerView
        get() = binding.recycleView

    override var isRefreshing: Boolean
        get() = binding.swiper.isRefreshing
        set(value) {
            binding.swiper.isRefreshing = value
        }

    override fun setLoadingColors(vararg colors: Int) {
        binding.swiper.setColorSchemeResources(*colors)
    }

    override fun setLoadingBackground(colorId: Int) {
        binding.swiper.setProgressBackgroundColorSchemeResource(colorId)
    }

    override fun setTriggerDistance(distance: Int) {
        binding.swiper.setDistanceToTriggerSync(distance)
    }

    override fun setMaxOffset(offset: Int) {
        binding.swiper.setSlingshotDistance(offset)
    }

    override fun setOnRefreshListener(func: () -> Unit) {
        binding.swiper.setOnRefreshListener { func.invoke() }
    }
}