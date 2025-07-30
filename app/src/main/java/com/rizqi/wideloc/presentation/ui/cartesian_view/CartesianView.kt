package com.rizqi.wideloc.presentation.ui.cartesian_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.rizqi.wideloc.databinding.DeviceTagBinding
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import timber.log.Timber
import kotlin.math.roundToInt

class CartesianView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private var backgroundBitmap: Bitmap? = null
    private var backgroundMatrix: Matrix? = null

    private var mapTransform: TrackingViewModel.MapTransform? = null
    private var axisScale: Double = 1.0
    private var pixelsPerUnit: Double = 1.0

    private var logicalBounds: LogicalBounds? = null

    private val axisPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 6f
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
        textSize = 28f
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
    private val viewPositionMap = mutableMapOf<String, Pair<Double, Double>>()

    data class LogicalBounds(
        val minX: Double,
        val maxX: Double,
        val minY: Double,
        val maxY: Double
    )

    fun setMapBackground(bitmap: Bitmap, matrix: Matrix) {
        backgroundBitmap = bitmap
        backgroundMatrix = matrix
        invalidate()
    }

    fun applyMapTransform(transform: TrackingViewModel.MapTransform) {
        if (width == 0 || height == 0) return
        this.mapTransform = transform
        this.axisScale = transform.axisScale

        val logicalWidth = transform.width
        val logicalHeight = transform.length

        val viewAspect = width.toDouble() / height.toDouble()
        val mapAspect = logicalWidth / logicalHeight

        val visibleWidth: Double
        val visibleHeight: Double

        if (mapAspect > viewAspect) {
            visibleWidth = logicalWidth
            visibleHeight = logicalWidth / viewAspect
        } else {
            visibleHeight = logicalHeight
            visibleWidth = logicalHeight * viewAspect
        }

        logicalBounds = LogicalBounds(
            minX = -visibleWidth / 2,
            maxX = visibleWidth / 2,
            minY = -visibleHeight / 2,
            maxY = visibleHeight / 2
        )

        pixelsPerUnit = width / visibleWidth

        invalidate()
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val bounds = logicalBounds ?: return
        val scale = pixelsPerUnit

        for ((id, view) in viewMap) {
            val (x, y) = viewPositionMap[id] ?: continue

            val screenX = ((x - bounds.minX) * scale).roundToInt()
            val screenY = ((bounds.maxY - y) * scale).roundToInt()

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
        backgroundBitmap?.let { bitmap ->
            backgroundMatrix?.let { matrix ->
                canvas.drawBitmap(bitmap, matrix, null)
            }
        }

        val bounds = logicalBounds ?: return
        val scale = pixelsPerUnit.toFloat()

        val originX = (-bounds.minX * scale).toFloat()
        val originY = (bounds.maxY * scale).toFloat()

        val widthPx = width.toFloat()
        val heightPx = height.toFloat()

        val spacing = (axisScale * scale).toFloat()
        val gridStartX = (bounds.minX / axisScale).toInt()
        val gridEndX = (bounds.maxX / axisScale).toInt()
        val gridStartY = (bounds.minY / axisScale).toInt()
        val gridEndY = (bounds.maxY / axisScale).toInt()

        for (i in gridStartX..gridEndX) {
            val x = originX + i * spacing
            if (i != 0) {
                val path = Path()
                path.moveTo(x, 0f)
                path.lineTo(x, heightPx)
                canvas.drawPath(path, gridPaint)
            }
            canvas.drawText((i * axisScale).toString(), x + 4f, originY - 8f, labelPaint)
        }

        for (j in gridStartY..gridEndY) {
            val y = originY - j * spacing
            if (j != 0) {
                val path = Path()
                path.moveTo(0f, y)
                path.lineTo(widthPx, y)
                canvas.drawPath(path, gridPaint)
            }
            canvas.drawText((j * axisScale).toString(), originX + 8f, y + 8f, labelPaint)
        }

        canvas.drawLine(0f, originY, widthPx, originY, axisPaint)
        canvas.drawLine(originX, 0f, originX, heightPx, axisPaint)

        canvas.drawText("x", widthPx - 28f, originY + 28f, axisLabelPaint)
        canvas.drawText("y", originX - 28f, 28f, axisLabelPaint)

        super.dispatchDraw(canvas)
    }

//    fun addOrUpdatePoint(id: String, view: View, x: Double, y: Double, zIndex: Int? = null) {
//        viewPositionMap[id] = x to y
//
//        if (!viewMap.containsKey(id)) {
//            viewMap[id] = view
//            if (zIndex != null) {
//                val safeIndex = zIndex.coerceIn(0, childCount)
//                addView(view, safeIndex)
//            } else {
//                addView(view)
//            }
//        } else {
//            if (zIndex != null) {
//                val currentIndex = indexOfChild(view)
//                val safeIndex = zIndex.coerceIn(0, childCount - 1)
//                if (currentIndex != safeIndex) {
//                    removeView(view)
//                    addView(view, safeIndex)
//                }
//            }
//        }
//        requestLayout()
//    }
fun addOrUpdatePoint(id: String, view: View, x: Double, y: Double, zIndex: Int? = null) {
    viewPositionMap[id] = x to y

    viewMap[id] = view
    removeView(view) // 💥 Always remove first to ensure layout reset

    if (zIndex != null) {
        val safeIndex = zIndex.coerceIn(0, childCount)
        addView(view, safeIndex)
    } else {
        addView(view)
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
}
