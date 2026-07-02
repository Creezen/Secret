package com.jayce.vexis.business.article

import android.view.View

interface OnContentChangeListener {

    fun onRemove(index: Int)

    fun onAdd(type: Int, index: Int, view: View)
}