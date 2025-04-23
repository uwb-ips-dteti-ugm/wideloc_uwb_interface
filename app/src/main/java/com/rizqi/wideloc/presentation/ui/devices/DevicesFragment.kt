package com.rizqi.wideloc.presentation.ui.devices

import android.os.Bundle
import android.view.View
import com.rizqi.wideloc.databinding.FragmentDevicesBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.devices.adapters.DevicesAdapter
import com.rizqi.wideloc.presentation.ui.devices.adapters.ReconfigureDevicesAdapter
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet

class DevicesFragment : BaseFragment<FragmentDevicesBinding>(FragmentDevicesBinding::inflate) {

    private val addDeviceBottomSheet = AddDeviceBottomSheet()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.devicesRecyclerViewFragmentDevices.adapter = DevicesAdapter()
        binding.reconfigureDevicesRecyclerViewFragmentDevices.adapter = ReconfigureDevicesAdapter()
        binding.addDeviceButtonDevices.setOnClickListener {
            addDeviceBottomSheet.show(parentFragmentManager, addDeviceBottomSheet.tag)
        }
    }
}