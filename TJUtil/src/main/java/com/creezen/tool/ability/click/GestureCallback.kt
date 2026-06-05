package com.creezen.tool.ability.click

interface GestureCallback {

    fun onZoomOut(viewId: String, scaleFactor: Float, point: Point): Boolean {
        return false
    }

    fun onZoomIn(viewId: String, scaleFactor: Float, point: Point): Boolean {
        return false
    }

    fun onUp(viewId: String, dist: Float): Boolean {
        return false
    }

    fun onDown(viewId: String, dist: Float): Boolean {
        return false
    }

    fun onLeft(viewId: String, dist: Float): Boolean {
        return false
    }

    fun onRight(viewId: String, dist: Float): Boolean {
        return false
    }

    fun onMove(viewId: String, points: Int, dx: Float, dy: Float): Boolean {
        return false
    }

    fun onDoubleClick(viewId: String): Boolean {
        return false
    }
}