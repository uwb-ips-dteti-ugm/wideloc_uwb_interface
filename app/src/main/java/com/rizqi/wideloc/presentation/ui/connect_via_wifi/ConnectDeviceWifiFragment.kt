package com.rizqi.wideloc.presentation.ui.connect_via_wifi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizqi.wideloc.databinding.FragmentConnectDeviceWifiBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters.AvailableWifiAdapter
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters.WifiInformation
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceViewModel
import com.rizqi.wideloc.receiver.WifiScanReceiver
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus
import com.rizqi.wideloc.utils.ViewUtils.isLocationEnabled

class ConnectDeviceWifiFragment :
    BaseFragment<FragmentConnectDeviceWifiBinding>(FragmentConnectDeviceWifiBinding::inflate) {

    private val addDeviceViewModel: AddDeviceViewModel by activityViewModels()

    private lateinit var wifiManager: WifiManager
    private lateinit var wifiScanReceiver: WifiScanReceiver
    private lateinit var availableWifiAdapter: AvailableWifiAdapter
    private var selectedWifiInformation: WifiInformation? = null
        set(value) {
            field = value
            binding.wifiPasswordInputLayoutConnectDeviceWifiFragment.visibility =
                if (value != null) View.VISIBLE else View.GONE
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            startWifiScan()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recalculateContentHeight()

        wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiScanReceiver = WifiScanReceiver(wifiManager) { scanResults ->
            onWifiScanResult(scanResults)
        }
        requireContext().registerReceiver(
            wifiScanReceiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )
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
        binding.connectButtonConnectDeviceWifiFragment.setOnClickListener {
            connectWifi()
        }
        binding.wifiPasswordInputEditTextConnectDeviceWifiFragment.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO){
                connectWifi()
                hideKeyboardAndClearFocus(requireActivity().currentFocus ?: binding.root)
                true
            } else {
                false
            }
        }
        binding.refreshButtonConnectDeviceWifiFragment.setOnClickListener { startWifiScan() }

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

    private fun recalculateContentHeight() {
        view?.post {
            (parentFragment?.parentFragment as? AddDeviceBottomSheet)?.recalculateHeight(
                listOf(
                    (parentFragment as? ConnectViaWiFiFragment)?.binding?.stepsIndicatorFragmentConnectViaWifi,
                    binding.root,
                ),
            )
        }
    }

    private fun requestWifiPermission() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        if (permissions.isNotEmpty()){
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            startWifiScan()
        }
    }

    private fun startWifiScan() {

        if (!isLocationEnabled(requireContext())){
            Toast.makeText(requireContext(), "Please! Enable location services", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            return
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val result = wifiManager.startScan()
        } else {
            Toast.makeText(
                requireContext(),
                "Fine location permission not granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun connectWifi() {
        val wifiInfo = selectedWifiInformation
        if (wifiInfo == null) {
            Toast.makeText(requireContext(), "Select a Wifi first", Toast.LENGTH_SHORT).show()
            return
        }
        val password = binding.wifiPasswordInputEditTextConnectDeviceWifiFragment.text.toString()
        if (password.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the Wi-Fi password", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+
            connectWifiPostAndroidQ(wifiInfo, password)
        } else {
            // For Android 9 and below
            connectWifiPreAndroidQ(wifiInfo, password)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectWifiPostAndroidQ(wifiInformation: WifiInformation?, password: String) {
        if (wifiInformation == null) {
            Toast.makeText(requireContext(), "Select a Wifi first", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isBlank()) {
            Toast.makeText(requireContext(), "Please! Enter the wifi password", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (!Settings.System.canWrite(requireContext())) {
            requestWriteSettingsPermission()
            return
        }

        val specifier = WifiNetworkSpecifier.Builder()
            .setSsid(wifiInformation.ssid)
            .setWpa2Passphrase(password)
            .build()

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(specifier)
            .build()

        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (!Settings.System.canWrite(requireContext())) return
                val result = connectivityManager.bindProcessToNetwork(network)
                if (result) {
                    Toast.makeText(
                        requireContext(),
                        "Connected to ${wifiInformation.ssid}",
                        Toast.LENGTH_SHORT
                    ).show()
                    addDeviceViewModel.setWifiInformation(wifiInformation)
                    (parentFragment as? ConnectViaWiFiFragment)?.goToNextPage()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to connect to ${wifiInformation.ssid}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onUnavailable() {
                super.onUnavailable()
                Toast.makeText(requireContext(), "Failed to connect", Toast.LENGTH_SHORT).show()
            }
        }

        connectivityManager.requestNetwork(request, networkCallback)

    }

    @Suppress("DEPRECATION")
    private fun connectWifiPreAndroidQ(wifiInformation: WifiInformation?, password: String) {
        val wifiConfig = android.net.wifi.WifiConfiguration().apply {
            SSID = "\"${wifiInformation?.ssid}\""
            preSharedKey = "\"$password\""
            allowedKeyManagement.set(android.net.wifi.WifiConfiguration.KeyMgmt.WPA_PSK)
        }

        val wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val netId = wifiManager.addNetwork(wifiConfig)
        if (netId != -1) {
            wifiManager.disconnect()
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
            Toast.makeText(requireContext(), "Connecting to ${wifiInformation?.ssid}...", Toast.LENGTH_SHORT).show()
            addDeviceViewModel.setWifiInformation(wifiInformation)
            (parentFragment as? ConnectViaWiFiFragment)?.goToNextPage()
        } else {
            Toast.makeText(requireContext(), "Failed to add network configuration for ${wifiInformation?.ssid}", Toast.LENGTH_SHORT).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestWriteSettingsPermission() {
        if (!Settings.System.canWrite(requireContext())) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = ("package:" + requireContext().packageName).toUri()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Toast.makeText(requireContext(), "Please allow WRITE_SETTINGS permission", Toast.LENGTH_LONG).show()
        }
    }

}