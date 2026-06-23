package com.jayce.vexis.client.ability.net

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.jayce.vexis.client.TLog

class ImageTarget(private val imageView: ImageView, private val debugImage: Boolean) : ImageViewTarget<Drawable>(imageView) {

    override fun onStart() { /**/ }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        imageView.setImageDrawable(resource)
    }

    override fun setResource(resource: Drawable?) { /**/ }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        TLog.d("onLoadFailed")
    }
}