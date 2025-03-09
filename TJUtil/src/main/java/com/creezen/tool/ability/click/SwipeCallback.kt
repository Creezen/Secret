package com.creezen.tool.ability.click

interface SwipeCallback {

    fun onPinchOut(viewId: String, scaleFactor: Float): Boolean {
        return true
    }

    fun onPinchIn(viewId: String, scaleFactor: Float): Boolean {
        return true
    }

    fun onUp(viewId: String, dist: Float): Boolean {
        return true
    }

    fun onDown(viewId: String, dist: Float): Boolean {
        return true
    }

    fun onLeft(viewId: String, dist: Float): Boolean {
        return true
    }

    fun onRight(viewId: String, dist: Float): Boolean {
        return true
    }

    fun onMove(viewId: String, points: Int): Boolean {
        return true
    }
}