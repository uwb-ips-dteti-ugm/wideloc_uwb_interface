package com.rizqi.wideloc.presentation.ui.connect_via_wifi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizqi.wideloc.databinding.FragmentConnectDeviceWifiBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters.AvailableWifiAdapter
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters.WifiInformation
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet
import com.rizqi.wideloc.receiver.WifiScanReceiver
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus
import kotlin.math.log

class ConnectDeviceWifiFragment : BaseFragment<FragmentConnectDeviceWifiBinding>(FragmentConnectDeviceWifiBinding::inflate) {

    private lateinit var wifiManager: WifiManager
    private lateinit var wifiScanReceiver: WifiScanReceiver
    private lateinit var availableWifiAdapter: AvailableWifiAdapter
    private var selectedWifiInformation: WifiInformation? = null
        set(value) {
            field = value
            binding.wifiPasswordInputLayoutConnectDeviceWifiFragment.visibility = if (value != null) View.VISIBLE else View.GONE
        }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        startWifiScan()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recalculateContentHeight()

        wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiScanReceiver = WifiScanReceiver(wifiManager){ scanResults ->
            onWifiScanResult(scanResults)
        }
        requireContext().registerReceiver(
            wifiScanReceiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )
        startWifiScan()
        availableWifiAdapter = AvailableWifiAdapter { wifiInfo ->
            selectedWifiInformation = wifiInfo
            binding.selectedWifiTextViewConnectDeviceWifiFragment.text = wifiInfo.ssid
        }

        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus(requireActivity().currentFocus ?: it)
        }
        binding.availableWifiRecyclerViewConnectDeviceWifiFragment.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = availableWifiAdapter
        }

        requestWifiPermission()
    }

    private fun onWifiScanResult(scanResults: List<ScanResult>) {
        val wifiInfos = scanResults.map {
            WifiInformation(
                ssid = it.SSID,
                ipv4 = it.BSSID,
            )
        }.distinct()
        availableWifiAdapter.submitList(wifiInfos.toMutableList())
        recalculateContentHeight()
    }

    private fun recalculateContentHeight(){
        view?.post {
            (parentFragment?.parentFragment as? AddDeviceBottomSheet)?.recalculateHeight(
                listOf(
                    (parentFragment as? ConnectViaWiFiFragment)?.binding?.stepsIndicatorFragmentConnectViaWifi,
                    binding.root,
                ),
            )
        }
    }

    private fun requestWifiPermission(){
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun startWifiScan(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val result = wifiManager.startScan()
        } else {
            Toast.makeText(requireContext(), "Fine location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

}