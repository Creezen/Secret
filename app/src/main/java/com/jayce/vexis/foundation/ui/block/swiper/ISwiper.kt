package com.jayce.vexis.foundation.ui.block.swiper

import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

interface ISwiper {

    val binding: ViewBinding

    val payload: RecyclerView

    var isRefreshing: Boolean

    fun setLoadingColors(@ColorRes vararg colors: Int)

    fun setLoadingBackground(@ColorRes colorId: Int)

    fun setTriggerDistance(distance: Int)

    fun setMaxOffset(offset: Int)

    fun setOnRefreshListener(func: () -> Unit)
}