package com.rizqi.wideloc.presentation.ui.cartesian_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.roundToInt

class CartesianView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    var yMin = -10
    var yMax = 10

    private var backgroundBitmap: Bitmap? = null
    private var backgroundMatrix: Matrix? = null

    fun setMapBackground(bitmap: Bitmap, matrix: Matrix) {
        backgroundBitmap = bitmap
        backgroundMatrix = matrix
        invalidate()
    }

    private val axisPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val gridPaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 1f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    private val labelPaint = Paint().apply {
        color = Color.BLACK
        textSize = 24f
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
    }

    private val axisLabelPaint = Paint().apply {
        color = Color.BLACK
        textSize = 28f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
    }

    private val viewMap = mutableMapOf<String, View>()
    private val viewPositionMap = mutableMapOf<String, Pair<Double, Double>>() // (x, y)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val originX = width / 2f
        val originY = height / 2f
        val yRange = yMax - yMin
        val ySpacing = height / yRange.toFloat()

        for ((id, view) in viewMap) {
            val (x, y) = viewPositionMap[id] ?: continue
            val screenX = (originX + x * ySpacing).roundToInt()
            val screenY = (originY - y * ySpacing).roundToInt()

            val childWidth = view.measuredWidth
            val childHeight = view.measuredHeight

            view.layout(
                screenX - childWidth / 2,
                screenY - childHeight / 2,
                screenX + childWidth / 2,
                screenY + childHeight / 2
            )
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        // Draw background map if available
        backgroundBitmap?.let { bitmap ->
            backgroundMatrix?.let { matrix ->
                canvas.drawBitmap(bitmap, matrix, null)
            }
        }

        val width = width.toFloat()
        val height = height.toFloat()
        val originX = width / 2
        val originY = height / 2

        val yRange = yMax - yMin
        val ySpacing = height / yRange

        axisPaint.strokeWidth = 6f
        labelPaint.textSize = 28f

        // X axis
        canvas.drawLine(0f, originY, width, originY, axisPaint)

        // Y axis
        canvas.drawLine(originX, 0f, originX, height, axisPaint)

        // Y grid lines
        for (i in yMin..yMax) {
            val y = originY - i * ySpacing
            if (i != 0) {
                val path = Path()
                path.moveTo(0f, y)
                path.lineTo(width, y)
                canvas.drawPath(path, gridPaint)
            }
            canvas.drawText(i.toString(), originX + 8f, y + 8f, labelPaint)
        }

        // X grid lines
        val xLabelCount = (width / ySpacing).toInt()
        val xMin = -xLabelCount / 2
        val xMax = xLabelCount / 2

        for (i in xMin..xMax) {
            val x = originX + i * ySpacing
            if (i != 0) {
                val path = Path()
                path.moveTo(x, 0f)
                path.lineTo(x, height)
                canvas.drawPath(path, gridPaint)
            }
            if (i != 0 || (yMin <= 0 && yMax >= 0)) {
                canvas.drawText(i.toString(), x + 4f, originY - 8f, labelPaint)
            }
        }

        // Axis labels
        canvas.drawText("x", width - 28f, originY + 28f, axisLabelPaint)
        canvas.drawText("y", originX - 28f, 28f, axisLabelPaint)

        // 👉 Now draw children (on top)
        super.dispatchDraw(canvas)
    }

    fun addOrUpdatePoint(
        id: String,
        view: View,
        x: Double,
        y: Double,
        zIndex: Int? = null
    ) {
        viewPositionMap[id] = x to y

        if (!viewMap.containsKey(id)) {
            viewMap[id] = view
            // Add new view at the specified zIndex
            if (zIndex != null) {
                val safeIndex = zIndex.coerceIn(0, childCount)
                addView(view, safeIndex)
            } else {
                addView(view) // Default: add to top
            }
        } else {
            // Already exists → reposition in z-order if needed
            if (zIndex != null) {
                val currentIndex = indexOfChild(view)
                val safeIndex = zIndex.coerceIn(0, childCount - 1)
                if (currentIndex != safeIndex) {
                    removeView(view)
                    addView(view, safeIndex)
                }
            }
            // Otherwise, we don’t re-add the view
        }

        requestLayout()
    }


    fun removePoint(id: String) {
        val view = viewMap.remove(id) ?: return
        viewPositionMap.remove(id)
        removeView(view)
        requestLayout()
    }

    fun clearPoints() {
        viewMap.clear()
        viewPositionMap.clear()
        removeAllViews()
        requestLayout()
    }

    fun setMapBackgroundAndMatrix(bitmap: Bitmap, matrix: Matrix) {
        backgroundBitmap = bitmap
        backgroundMatrix = matrix
        invalidate()
    }
}
