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
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import com.rizqi.wideloc.domain.model.MapTransform
import kotlin.math.roundToInt

class CartesianView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private var backgroundBitmap: Bitmap? = null
    private var backgroundMatrix: Matrix? = null

    private var mapTransform: MapTransform? = null
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

    private var routeMap: Map<String, List<Pair<Double, Double>>> = emptyMap()

    private val routePaints = listOf(
        Paint().apply { color = Color.BLUE; strokeWidth = 4f; style = Paint.Style.STROKE },
        Paint().apply { color = Color.RED; strokeWidth = 4f; style = Paint.Style.STROKE },
        Paint().apply { color = Color.GREEN; strokeWidth = 4f; style = Paint.Style.STROKE },
        Paint().apply { color = Color.MAGENTA; strokeWidth = 4f; style = Paint.Style.STROKE },
        Paint().apply { color = Color.CYAN; strokeWidth = 4f; style = Paint.Style.STROKE },
    )

    private var cachedScreenRoutes: Map<String, List<Pair<Double, Double>>> = emptyMap()

    fun setRoutes(routeMap: Map<String, List<Pair<Double, Double>>>) {
        if (this.routeMap == routeMap) return // skip update

        this.routeMap = routeMap

        // Cache screen coordinates
        val bounds = logicalBounds ?: return
        val scale = pixelsPerUnit
        cachedScreenRoutes = routeMap.mapValues { (_, route) ->
            route.map { (x, y) ->
                ((x - bounds.minX) * scale) to ((bounds.maxY - y) * scale)
            }
        }

        invalidate()
    }


    fun setMapBackground(bitmap: Bitmap, matrix: Matrix) {
        backgroundBitmap = bitmap
        backgroundMatrix = matrix
        invalidate()
    }

    fun applyMapTransform(transform: MapTransform) {
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

        cachedScreenRoutes.entries.forEachIndexed { index, (_, screenRoute) ->
            if (screenRoute.size < 2 || screenRoute.all { it == screenRoute[0] }) return@forEachIndexed

            val path = Path().apply {
                moveTo(screenRoute[0].first.toFloat(), screenRoute[0].second.toFloat())
                for (i in 1 until screenRoute.size) {
                    lineTo(screenRoute[i].first.toFloat(), screenRoute[i].second.toFloat())
                }
            }

            val paint = routePaints[index % routePaints.size]
            canvas.drawPath(path, paint)
        }

        super.dispatchDraw(canvas)
    }


    fun addOrUpdatePoint(id: String, view: View, x: Double, y: Double, zIndex: Int? = null) {
        viewPositionMap[id] = x to y

        viewMap[id]?.let { oldView ->
            removeView(oldView)
        }

        viewMap[id] = view

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

    data class LogicalBounds(
        val minX: Double,
        val maxX: Double,
        val minY: Double,
        val maxY: Double
    )
}
