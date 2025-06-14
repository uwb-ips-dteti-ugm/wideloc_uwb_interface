package com.rizqi.wideloc.receiver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WifiInfoReceiver(
    private val context: Context,
    private val activityResultCaller: ActivityResultCaller,
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager,
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager,
) {
    private var permissionCallback: ((Boolean) -> Unit)? = null
    private var wifiChangeListener: ((Pair<String, WifiInfo>?) -> Unit)? = null

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    private val permissionLauncher =
        activityResultCaller.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val granted = result.all { it.value }
            permissionCallback?.invoke(granted)
        }

    fun startListening(onWifiChanged: (Pair<String, WifiInfo>?) -> Unit) {
        wifiChangeListener = onWifiChanged

        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                registerCallback()
            } else {
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
                onWifiChanged(null)
            }
        } else {
            permissionCallback = { granted ->
                if (granted) {
                    registerCallback()
                } else {
                    onWifiChanged(null)
                }
            }
            permissionLauncher.launch(permissions)
        }
    }

    fun stopListening() {
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        networkCallback = null
    }

    @SuppressLint("MissingPermission")
    private fun registerCallback() {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        networkCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            object : ConnectivityManager.NetworkCallback(NetworkCallback.FLAG_INCLUDE_LOCATION_INFO) {
                override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                    wifiChangeListener?.invoke(extractWifiInfo(network, capabilities))
                }

                override fun onLost(network: Network) {
                    wifiChangeListener?.invoke(null)
                }
            }
        } else {
            object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                    wifiChangeListener?.invoke(extractWifiInfo(network, capabilities))
                }

                override fun onLost(network: Network) {
                    wifiChangeListener?.invoke(null)
                }
            }
        }

        connectivityManager.registerNetworkCallback(request, networkCallback!!)
    }

    @SuppressLint("MissingPermission")
    private fun extractWifiInfo(
        network: Network,
        capabilities: NetworkCapabilities
    ): Pair<String, WifiInfo>? {
        val ipAddress = connectivityManager.getLinkProperties(network)
            ?.linkAddresses
            ?.firstOrNull { it.address.hostAddress?.contains('.') == true }
            ?.address
            ?.hostAddress

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val wifiInfo = capabilities.transportInfo as? WifiInfo
            if (ipAddress != null && wifiInfo != null) {
                return ipAddress to wifiInfo
            }
        }

        return null
    }
}
