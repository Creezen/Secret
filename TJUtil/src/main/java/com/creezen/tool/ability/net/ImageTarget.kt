package com.creezen.tool.ability.net

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.creezen.tool.TLog

class ImageTarget(private val imageView: ImageView, private val debugImage: Boolean) : CustomTarget<Drawable>() {

    private var start: Long = 0L

    override fun onStart() { /**/ }

    override fun onLoadStarted(placeholder: Drawable?) {
        super.onLoadStarted(placeholder)
        if (!debugImage) return
        start = System.nanoTime()
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        if (debugImage) {
            val duration = (System.nanoTime() - start) / 1000000
            TLog.d("Glide image load duration: ${duration}ms")
        }
        imageView.setImageDrawable(resource)
    }

    override fun onLoadCleared(placeholder: Drawable?) { /**/ }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        TLog.d("onLoadFailed")
    }
}