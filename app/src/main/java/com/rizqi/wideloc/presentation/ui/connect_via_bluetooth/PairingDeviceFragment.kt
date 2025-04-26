package com.rizqi.wideloc.presentation.ui.connect_via_bluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rizqi.wideloc.databinding.FragmentPairingDeviceBinding
import com.rizqi.wideloc.presentation.ui.connect_via_bluetooth.adapters.AvailableDevicesAdapter
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus

class PairingDeviceFragment : Fragment() {

    private var _binding: FragmentPairingDeviceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPairingDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            (parentFragment?.parentFragment as? AddDeviceBottomSheet)?.recalculateHeight(
                listOf(
                    (parentFragment as? ConnectViaBluetoothFragment)?.binding?.stepsIndicatorFragmentConnectViaBluetooth,
                    binding.root,
                ),
            )
        }

        binding.availableDevicesRecyclerViewFragmentPairingDevice.adapter = AvailableDevicesAdapter()

        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus(requireActivity().currentFocus ?: it)
        }

        binding.pairButtonFragmentPairingDevice.setOnClickListener {
            (parentFragment as? ConnectViaBluetoothFragment)?.goToNextPage()
        }
    }

}