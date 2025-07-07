package com.rizqi.wideloc.utils

import android.content.Context
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

