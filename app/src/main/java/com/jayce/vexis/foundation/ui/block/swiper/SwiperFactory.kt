package com.jayce.vexis.foundation.ui.block.swiper

import android.content.Context
import android.view.ViewGroup

object SwiperFactory {

    fun get(context: Context, parent: ViewGroup, type: SwiperType): ISwiper {
        return when (type) {
            SwiperType.DEFAULT -> DefaultSwiperRecycle(context, parent)
            SwiperType.HORIZONTAL -> HorizontalSwiperRecycle(context, parent)
        }
    }

    enum class SwiperType {
        DEFAULT,
        HORIZONTAL;

        companion object {
            fun of(type: Int) = when (type) {
                0 -> DEFAULT
                1 -> HORIZONTAL
                else -> DEFAULT
            }
        }
    }
}