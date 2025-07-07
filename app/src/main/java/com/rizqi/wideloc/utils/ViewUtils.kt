package com.rizqi.wideloc.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

object ViewUtils {

    fun hideKeyboardAndClearFocus(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(
            /* source = */ bitmap,
            /* x = */ 0,
            /* y = */ 0,
            /* width = */ bitmap.width,
            /* height = */ bitmap.height,
            /* m = */ matrix,
            /* filter = */ true
        )
    }

    fun flipBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply { preScale(-1f, 1f) }
        return Bitmap.createBitmap(
            /* source = */ bitmap,
            /* x = */ 0,
            /* y = */ 0,
            /* width = */ bitmap.width,
            /* height = */ bitmap.height,
            /* m = */ matrix,
            /* filter = */ true
        )
    }
}

class WrapContentLinearLayoutManager(
    context: Context,
    orientation: Int,
    reverseLayout: Boolean
) : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        var maxHeight = 0
        for (i in 0 until itemCount.coerceAtMost(10)) { // Limit to first 10 items for performance
            val view = recycler.getViewForPosition(i)
            measureChildWithMargins(view, widthSpec, heightSpec)
            val height = getDecoratedMeasuredHeight(view)
            if (height > maxHeight) maxHeight = height
            recycler.recycleView(view)
        }
        val width = View.MeasureSpec.getSize(widthSpec)
        setMeasuredDimension(width, maxHeight)
    }
}

