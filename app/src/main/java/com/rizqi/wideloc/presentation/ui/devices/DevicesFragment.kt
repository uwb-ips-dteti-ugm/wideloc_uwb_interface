package com.rizqi.wideloc.presentation.ui.devices

import android.os.Bundle
import android.view.View
import com.rizqi.wideloc.databinding.FragmentDevicesBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.devices.adapters.DevicesAdapter
import com.rizqi.wideloc.presentation.ui.devices.adapters.ReconfigureDevicesAdapter

class DevicesFragment : BaseFragment<FragmentDevicesBinding>(FragmentDevicesBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.devicesRecyclerViewFragmentDevices.adapter = DevicesAdapter()
        binding.reconfigureDevicesRecyclerViewFragmentDevices.adapter = ReconfigureDevicesAdapter()
    }
}