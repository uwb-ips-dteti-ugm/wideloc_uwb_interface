package com.rizqi.wideloc.presentation.ui.cartesian_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

class CartesianView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var yMin = -10
    var yMax = 10

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val originX = width / 2
        val originY = height / 2

        // Adjust paint for thicker axes
        axisPaint.strokeWidth = 6f
        labelPaint.textSize = 28f

        // Draw X and Y axes (solid lines)
        canvas.drawLine(0f, originY, width, originY, axisPaint) // X axis
        canvas.drawLine(originX, 0f, originX, height, axisPaint) // Y axis

        // Y axis range and spacing
        val yRange = yMax - yMin
        val ySpacing = height / yRange

        // Horizontal grid lines + Y labels
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

        // X axis range depends on Y spacing
        val xLabelCount = (width / ySpacing).toInt()
        val xMin = -xLabelCount / 2
        val xMax = xLabelCount / 2

        // Vertical grid lines + X labels
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

        // --- Axis Titles ---

        // X - right top of axis
        canvas.drawText("x", width - 28f, originY + 28f, axisLabelPaint)

        // Y - top of axis (left of Y line)
        canvas.drawText("y", originX - 28f, 28f, axisLabelPaint)
    }
}