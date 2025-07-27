package com.jayce.vexis.foundation.view.animator

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class MyCustomTransformer : ViewPager2.PageTransformer {

    override fun transformPage(
        page: View,
        position: Float,
    ) {
        val width = page.width
        val height = page.height
        page.pivotX = (width / 2).toFloat()
        page.pivotY = (height / 2).toFloat()
        if (position < -1 || position > 1) {
            page.scaleX = 0.5f
            page.scaleY = 0.5f
            page.pivotY = (height / 2).toFloat()
            return
        }
        if (position < 0) {
            val scale = ((1 + position) * 0.5 + 0.5).toFloat()
            page.scaleX = scale
            page.scaleY = scale
            page.alpha = 1 + position
            page.pivotX = width.toFloat()
        } else {
            val scale = ((1 - position) * 0.5 + 0.5).toFloat()
            page.scaleX = scale
            page.scaleY = scale
            page.alpha = 1 - position
            page.pivotX = (width * (1 - position) * 0.5).toFloat()
        }
    }
}