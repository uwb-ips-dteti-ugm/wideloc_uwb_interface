package com.rizqi.wideloc.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

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
