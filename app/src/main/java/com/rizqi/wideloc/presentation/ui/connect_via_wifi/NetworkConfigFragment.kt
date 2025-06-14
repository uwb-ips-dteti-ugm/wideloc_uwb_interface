package com.rizqi.wideloc.presentation.ui.connect_via_wifi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.FragmentNetworkConfigBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.connect_via_bluetooth.ConnectViaBluetoothFragment
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceViewModel

class NetworkConfigFragment : BaseFragment<FragmentNetworkConfigBinding>(FragmentNetworkConfigBinding::inflate) {

    private val addDeviceViewModel: AddDeviceViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (parentFragment?.parentFragment as? AddDeviceBottomSheet)?.toggleWifiInfoVisibility(true)
        recalculateContentHeight()

        binding.dnsInputLayoutNetworkConfigFragment.prefixText = getPrefix()
        binding.apSSIDInputLayoutNetworkConfigFragment.prefixText = getPrefix()
    }

    private fun recalculateContentHeight() {
        view?.post {
            (parentFragment?.parentFragment as? AddDeviceBottomSheet)?.recalculateHeight(
                listOf(
                    (parentFragment as ConnectViaWiFiFragment).binding.stepsIndicatorFragmentConnectViaWifi,
                    binding.root,
                ),
            )
        }
    }

    private fun getPrefix(): String {
        val role = addDeviceViewModel.deviceSetupModel.value?.role?.name ?: "role_unknown"
        val name = addDeviceViewModel.deviceSetupModel.value?.name ?: "name_unknown"
        val id = addDeviceViewModel.id.value ?: "id_unknown"
        return "$id-$role-$name"
    }
}