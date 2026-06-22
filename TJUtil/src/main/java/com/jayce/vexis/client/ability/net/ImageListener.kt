package com.jayce.vexis.client.ability.net

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jayce.vexis.client.TLog

class ImageListener(private val debug: Boolean) : RequestListener<Drawable> {

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        TLog.d("Glide load image failed: ${e?.message}")
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        if (!debug) return false
        val source = when (dataSource) {
            DataSource.MEMORY_CACHE -> "内存缓存"
            DataSource.RESOURCE_DISK_CACHE -> "磁盘缓存"
            DataSource.DATA_DISK_CACHE -> "原始数据磁盘缓存"
            DataSource.REMOTE -> "网络"
            DataSource.LOCAL -> "本地资源"
            else -> "未知"
        }
        TLog.d("Glide图片资源来源： $source($dataSource)")
        return false
    }
}