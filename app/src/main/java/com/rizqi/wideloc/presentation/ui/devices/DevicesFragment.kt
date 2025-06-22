package com.rizqi.wideloc.presentation.ui.devices

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rizqi.wideloc.databinding.FragmentDevicesBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.devices.adapters.DevicesAdapter
import com.rizqi.wideloc.presentation.ui.devices.adapters.ReconfigureDevicesAdapter
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DevicesFragment : BaseFragment<FragmentDevicesBinding>(FragmentDevicesBinding::inflate) {

    private val addDeviceBottomSheet = AddDeviceBottomSheet()

    private val viewModel: DevicesViewModel by viewModels()

    private lateinit var devicesAdapter: DevicesAdapter
    private lateinit var reconfigureDevicesAdapter: ReconfigureDevicesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        devicesAdapter = DevicesAdapter()
        reconfigureDevicesAdapter = ReconfigureDevicesAdapter()

        binding.devicesRecyclerViewFragmentDevices.adapter = devicesAdapter
        binding.reconfigureDevicesRecyclerViewFragmentDevices.adapter = reconfigureDevicesAdapter
        binding.addDeviceButtonDevices.setOnClickListener {
            addDeviceBottomSheet.show(parentFragmentManager, addDeviceBottomSheet.tag)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.availableDevices.collect {
                    devicesAdapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reconfigureDevices.collect {
                    reconfigureDevicesAdapter.submitList(it)
                }
            }
        }


    }
}