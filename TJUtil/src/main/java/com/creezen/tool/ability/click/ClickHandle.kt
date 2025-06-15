package com.creezen.tool.ability.click

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class ClickHandle(private val mode: Mode) {

    companion object {
        const val TAG = "ClickHandle"
    }

    private var _viewId = ""

    private var pointNum = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastDist = 0f
    private var isSet: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    fun registerSwipeEvent(viewId: String, view: View, callback: SwipeCallback) {
        if(isSet) {
            Log.e(TAG,"A handle is bind with only one view, please instantiate another handle")
            return
        }
        _viewId = viewId
        when (mode) {
            Mode.LISTENER -> {
                view.setOnTouchListener { _, event ->
                    return@setOnTouchListener handleViewClick(viewId, event, callback)
                }
            }
            else -> {}
        }
        isSet = true
    }

    fun unregisterSwipeEvent(viewId: String) {
        isSet = false
        _viewId = ""
    }

    fun handleViewClick(viewId: String, event: MotionEvent, callback: SwipeCallback): Boolean {
        if(_viewId != viewId || _viewId.isBlank()) {
            return true
        }
        var handleFlag = true
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pointNum ++
            }
            MotionEvent.ACTION_UP -> {
                pointNum --
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                pointNum ++
                lastDist = calculatePointDistance(event)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                pointNum --
                lastDist = -1f
            }
            MotionEvent.ACTION_MOVE -> {
                callback.onMove(viewId, pointNum)
                handleFlag = handleEvent(viewId, event, callback)
                if(pointNum >= 2) {
                    lastDist = calculatePointDistance(event)
                }
            }
        }
        lastX = event.x
        lastY = event.y
        return handleFlag
    }

    private fun handleEvent(viewId: String, event: MotionEvent, callback: SwipeCallback): Boolean {
        var flag = true
        val deltaX = event.x - lastX
        val deltaY = event.y - lastY
        if(pointNum == 1) {
            flag = handleOnePoint(viewId, deltaX, deltaY, callback)
        }
        if(pointNum >= 2) {
            flag = handleMultiPoint(viewId, event, callback)
        }
        return flag
    }

    private fun handleOnePoint(viewId: String, deltaX: Float, deltaY: Float, callback: SwipeCallback): Boolean {
        if(abs(deltaX) < 5 && abs(deltaY) < 5) return true
        val flag: Boolean
        if(abs(deltaX) > abs(deltaY)) {
            if(deltaX > 0)
                flag = callback.onRight(viewId, deltaX)
            else
                flag = callback.onLeft(viewId, deltaX)
        } else {
            if(deltaY < 0)
                flag = callback.onUp(viewId, deltaY)
            else
                flag = callback.onDown(viewId, deltaY)
        }
        return flag
    }

    private fun handleMultiPoint(viewId: String, event: MotionEvent, callback: SwipeCallback): Boolean {
        val flag: Boolean
        if(lastDist < 0) return true
        val distance = calculatePointDistance(event)
        if(abs(distance - lastDist) < 2 || distance <= 0) return true
        if(distance < lastDist)
            flag = callback.onPinchIn(viewId, distance / lastDist)
        else
            flag = callback.onPinchOut(viewId, distance / lastDist)
        lastDist = distance
        return flag
    }

    private fun calculatePointDistance(event: MotionEvent): Float {
        if(event.pointerCount < 2) return 0f
        return calculateDistance(
            event.getX(0),
            event.getY(0),
            event.getX(1),
            event.getY(1)
        )
    }

    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val powerX = (x1 - x2).toDouble().pow(2.0).toFloat()
        val powerY = (y1 - y2).toDouble().pow(2.0).toFloat()
        return sqrt(powerX + powerY)
    }

    enum class Mode {
        INTERCEPT, LISTENER
    }
}