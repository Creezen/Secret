package com.jayce.vexis.client.ability.click

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.client.TLog
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class GestureHandle(private val mode: Mode = Mode.LISTENER) {

    private var _viewId = NIL

    private var lastClick: Long = 0L
    private var pointNum = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastDist = 0f
    private var lastMoveX = -1f
    private var lastMoveY = -1f
    private var isSet: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    fun registerGestureEvent(viewId: String, view: View, callback: GestureCallback) {
        if(isSet) {
            TLog.e("A handle is bind with only one view, please instantiate another handle")
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

    fun unregisterGestureEvent(viewId: String) {
        isSet = false
        _viewId = NIL
    }

    private fun handleViewClick(viewId: String, event: MotionEvent, callback: GestureCallback): Boolean {
        if(_viewId != viewId || _viewId.isBlank()) return true
        val handleFlag: Boolean
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val stamp = System.currentTimeMillis()
                handleFlag = if (stamp - lastClick < 300) {
                    callback.onDoubleClick(viewId)
                } else {
                    true
                }
                lastMoveX = event.x
                lastMoveY = event.y
                lastClick = stamp
                pointNum ++
            }
            MotionEvent.ACTION_UP -> {
                pointNum --
                handleFlag = false
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                pointNum ++
                lastDist = calculatePointDistance(event).first
                handleFlag = false
            }
            MotionEvent.ACTION_POINTER_UP -> {
                pointNum --
                lastDist = -1f
                handleFlag = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastMoveX
                val dy = event.y - lastMoveY
                if (lastMoveX >= 0 && lastMoveY >= 0 && pointNum == 1) {
                    callback.onMove(viewId, pointNum, dx, dy)
                }
                lastMoveX = event.x
                lastMoveY = event.y
                handleFlag = handleEvent(viewId, event, callback)
                if(pointNum >= 2) {
                    lastDist = calculatePointDistance(event).first
                }
            }
            else -> {
                handleFlag = false
            }
        }
        lastX = event.x
        lastY = event.y
        return handleFlag
    }

    private fun handleEvent(viewId: String, event: MotionEvent, callback: GestureCallback): Boolean {
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

    private fun handleOnePoint(viewId: String, deltaX: Float, deltaY: Float, callback: GestureCallback): Boolean {
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

    private fun handleMultiPoint(viewId: String, event: MotionEvent, callback: GestureCallback): Boolean {
        val flag: Boolean
        if(lastDist < 0) return true
        val pair = calculatePointDistance(event)
        val distance = pair.first
        val point = pair.second
        if(abs(distance - lastDist) < 2 || distance <= 0) return true
        if(distance < lastDist)
            flag = callback.onZoomIn(viewId, distance / lastDist, point)
        else
            flag = callback.onZoomOut(viewId, distance / lastDist, point)
        lastDist = distance
        return flag
    }

    private fun calculatePointDistance(event: MotionEvent): Pair<Float, Point> {
        if(event.pointerCount < 2) return 0f to Point(0f, 0f)
        val centerX = (event.getX(0) + event.getX(1)) * 0.5f
        val centerY = (event.getY(0) + event.getY(1)) * 0.5f
        val point = Point(centerX, centerY)
        return calculateDistance(
            event.getX(0),
            event.getY(0),
            event.getX(1),
            event.getY(1)
        ) to point
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