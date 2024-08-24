package com.jayce.vexis.utility.maze

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.Stack
import kotlin.math.absoluteValue

class MazeView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    private val paint = Paint()
    private val playerPaint = Paint()
    private lateinit var bitmap: Bitmap
    private val bitmapCanvas: Canvas = Canvas()
    private var row: Int = -1
    private var line: Int = -1
    private var initStatus = 0
    private lateinit var mazeMatrix: Array<Array<GridItem>>
    private val visitStack = Stack<GridItem>()
    private var gridWidth = -1f
    private var previewX = 0
    private var previewY = 0
    private var downX = -1f
    private var downY = -1f
    private var playerX = 0
    private var playerY = 0
    private var statusCallback: MazeStatusCallback? = null

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 1f
        playerPaint.strokeWidth = 3f
    }

    override fun onDraw(canvas: Canvas) {
        if(initStatus == 0) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmapCanvas.setBitmap(bitmap)
            return
        }
        if(initStatus == 1) {
            drawMazaMap()
            bitmapCanvas.drawRect(
                playerY * gridWidth + paint.strokeWidth * 0.5f + playerPaint.strokeWidth * 1.2f,
                playerX * gridWidth + paint.strokeWidth * 0.5f + playerPaint.strokeWidth * 1.2f,
                (playerY + 1) * gridWidth - paint.strokeWidth * 0.5f - playerPaint.strokeWidth * 1.2f,
                (playerX + 1) * gridWidth - paint.strokeWidth * 0.5f - playerPaint.strokeWidth * 1.2f,
                playerPaint
            )
        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }

    private fun drawMazaMap() {
        row = height / gridWidth.toInt()
        line = width / gridWidth.toInt()
        val remainX = width - line * gridWidth
        val remainY = height - row * gridWidth
        bitmapCanvas.translate(remainX * 0.5f, remainY * 0.5f)
        initMazeMatrix()
        generateMaze(0, 0)
        mazeMatrix.forEach { mazeLine ->
            mazeLine.forEach {
                drawLine(bitmapCanvas, it)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event == null) {
            return true
        }
        initStatus = 2
        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val horizon = event.x - downX
                val vertical = event.y - downY
                val absX = horizon.absoluteValue
                val absY = vertical.absoluteValue
                if(absX > absY) {
                    if((horizon > 0 && mazeMatrix[playerX][playerY].right)
                        ||(horizon < 0 && mazeMatrix[playerX][playerY].left)) {
                        statusCallback?.onError()
                        return true
                    }
                    playerY += (horizon / absX).toInt()
                } else {
                    if((vertical > 0 && mazeMatrix[playerX][playerY].bottom)
                        ||(vertical < 0 && mazeMatrix[playerX][playerY].top)) {
                        statusCallback?.onError()
                        return true
                    }
                    playerX += (vertical / absY).toInt()
                }
                if(playerX == row - 1 && playerY == line - 1) {
                    statusCallback?.onFinish()
                } else {
                    statusCallback?.onMove()
                }
                handlePlayer()
                invalidate()
            }
        }
        return true
    }

    private fun handlePlayer() {
        bitmapCanvas.save()
        bitmapCanvas.clipRect(
            previewY * gridWidth + paint.strokeWidth * 0.6f,
            previewX * gridWidth + paint.strokeWidth * 0.6f,
            (previewY + 1) * gridWidth - paint.strokeWidth * 0.6f,
            (previewX + 1) * gridWidth - paint.strokeWidth * 0.6f
        )
        bitmapCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        bitmapCanvas.restore()
        previewX = playerX
        previewY = playerY
        paint.color = Color.RED
        bitmapCanvas.drawRect(
            playerY * gridWidth + paint.strokeWidth * 0.5f + playerPaint.strokeWidth * 1.2f,
            playerX * gridWidth + paint.strokeWidth * 0.5f + playerPaint.strokeWidth * 1.2f,
            (playerY + 1) * gridWidth - paint.strokeWidth * 0.5f - playerPaint.strokeWidth * 1.2f,
            (playerX + 1) * gridWidth - paint.strokeWidth * 0.5f - playerPaint.strokeWidth * 1.2f,
            playerPaint
        )
    }

    private fun initMazeMatrix() {
        mazeMatrix = Array(row) { Array(line) {GridItem()} }
        for (i in 0 until  row) {
            for (j in 0 until line) {
                mazeMatrix[i][j].x = i
                mazeMatrix[i][j].y = j
            }
        }
        mazeMatrix[0][0].left = false
        mazeMatrix[row - 1][line - 1].right = false
    }

    private fun drawLine(canvas: Canvas, item: GridItem) {
        with(item) {
            if(top) {
                canvas.drawLine(y * gridWidth, x * gridWidth,
                    (y + 1) * gridWidth, x * gridWidth,
                    paint
                )
            }
            if(right) {
                canvas.drawLine((y + 1) * gridWidth, x * gridWidth,
                    (y + 1) * gridWidth, (x + 1) * gridWidth,
                    paint
                )
            }
            if(bottom) {
                canvas.drawLine(y * gridWidth, (x + 1) * gridWidth,
                    (y + 1) * gridWidth, (x + 1) * gridWidth,
                    paint
                )
            }
            if(left) {
                canvas.drawLine(y * gridWidth, x * gridWidth,
                    y * gridWidth, (x + 1) * gridWidth,
                    paint
                )
            }
        }
    }

    private fun generateMaze(x: Int, y: Int) {
        if(!mazeMatrix[x][y].visit) {
            visitStack.push(mazeMatrix[x][y])
            mazeMatrix[x][y].visit = true
        }
        val validGrid = arrayListOf<PointBean>()
        setVisitableGrid(validGrid, x, y)
        if(validGrid.isEmpty()) {
            if (visitStack.isEmpty()) {
                return
            }
            val grid = visitStack.pop()
            generateMaze(grid.x, grid.y)
            return
        }
        validGrid.shuffle()
        val nextVisit = validGrid[0]
        makeWall(x, y, nextVisit)
        generateMaze(x + nextVisit.x, y + nextVisit.y)
    }

    private fun makeWall(x: Int, y: Int, pointBean: PointBean) {
        if(pointBean.x == -1 && pointBean.y == 0) {
            mazeMatrix[x][y].top = false
            mazeMatrix[x - 1][y].bottom = false
            return
        }
        if(pointBean.x == 0 && pointBean.y == 1) {
            mazeMatrix[x][y].right = false
            mazeMatrix[x][y + 1].left = false
            return
        }
        if(pointBean.x == 1 && pointBean.y == 0) {
            mazeMatrix[x][y].bottom = false
            mazeMatrix[x + 1][y].top = false
            return
        }
        if(pointBean.x == 0 && pointBean.y == -1) {
            mazeMatrix[x][y].left = false
            mazeMatrix[x][y - 1].right = false
        }
    }

    private fun isCoordinateValid(x: Int, y: Int): Boolean {
        return (x >= 0 && y >= 0 && x < row && y < line)
    }

    private fun isValidGrid(x: Int, y: Int): Boolean {
        return isCoordinateValid(x, y) && !mazeMatrix[x][y].visit
    }

    private fun setVisitableGrid(validGrid: ArrayList<PointBean>, x: Int, y: Int) {
        if(isValidGrid(x - 1, y)) {
            validGrid.add(PointBean(-1, 0))
        }
        if(isValidGrid(x, y + 1)) {
            validGrid.add(PointBean(0, 1))
        }
        if(isValidGrid(x + 1, y)) {
            validGrid.add(PointBean(1, 0))
        }
        if(isValidGrid(x, y - 1)) {
            validGrid.add(PointBean(0, -1))
        }
    }

    fun setRowAndLine(width: Float) {
        this.gridWidth = width
        initStatus = 1
    }

    fun registerCallback(callback: MazeStatusCallback) {
        this.statusCallback = callback
    }
}