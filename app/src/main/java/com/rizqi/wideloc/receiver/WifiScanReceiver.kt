package com.rizqi.wideloc.receiver

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission

class WifiScanReceiver(
    private val wifiManager: WifiManager,
    private val onScanResult: (List<ScanResult>) -> Unit
) : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun onReceive(context: Context?, intent: Intent?) {
        val scanSuccess = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
        } else {
            intent?.action?.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        }
        if (scanSuccess == true){
            val scanResults = wifiManager.scanResults
            onScanResult(scanResults)
        }
    }
}