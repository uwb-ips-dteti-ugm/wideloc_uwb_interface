package com.rizqi.wideloc.presentation.ui.connect_via_wifi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.FragmentNetworkConfigBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.connect_via_bluetooth.ConnectViaBluetoothFragment
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceViewModel
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus

class NetworkConfigFragment : BaseFragment<FragmentNetworkConfigBinding>(FragmentNetworkConfigBinding::inflate) {

    private val addDeviceViewModel: AddDeviceViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (parentFragment?.parentFragment as? AddDeviceBottomSheet)?.toggleWifiInfoVisibility(true)
        recalculateContentHeight()

        addDeviceViewModel.networkConfig.observe(viewLifecycleOwner) { networkConfig ->
            if (networkConfig.isApSSIDSameAsDNS) {
                val currentText = binding.apSSIDInputEditTextNetworkConfigFragment.text?.toString()
                val newText = networkConfig.dns
                if (currentText != newText) {
                    binding.apSSIDInputEditTextNetworkConfigFragment.setText(newText)
                }
            }
        }
        addDeviceViewModel.networkConfigError.observe(viewLifecycleOwner){ error ->
            binding.dnsInputLayoutNetworkConfigFragment.error = error?.dns
            binding.portInputLayoutNetworkConfigFragment.error = error?.port
            binding.apSSIDInputLayoutNetworkConfigFragment.error = error?.apSSID
            binding.apPasswordInputLayoutConnectDeviceWifiFragment.error = error?.apPassword
            binding.staSSIDInputLayoutNetworkConfigFragment.error = error?.staSSID
            binding.staPasswordInputLayoutConnectDeviceWifiFragment.error = error?.staPassword
        }

        binding.dnsInputLayoutNetworkConfigFragment.prefixText = addDeviceViewModel.getNamePrefix()
        binding.apSSIDInputLayoutNetworkConfigFragment.prefixText = addDeviceViewModel.getNamePrefix()
        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus(requireActivity().currentFocus ?: it)
        }
        binding.dnsInputEditTextNetworkConfigFragment.doOnTextChanged { text, _, _, _ ->
            addDeviceViewModel.setDNS(text.toString())
        }
        binding.portInputEditTextNetworkConfigFragment.doOnTextChanged { text, _, _, _ ->
            addDeviceViewModel.setPort(text.toString())
        }
        binding.apSSIDInputEditTextNetworkConfigFragment.doOnTextChanged { text, _, _, _ ->
            addDeviceViewModel.setAPSSID(text.toString())
        }
        binding.apSSIDCheckBoxNetworkConfigFragment.setOnCheckedChangeListener { _, isChecked ->
            binding.apSSIDInputLayoutNetworkConfigFragment.isEnabled = !isChecked
            binding.apSSIDInputEditTextNetworkConfigFragment.isEnabled = !isChecked

            addDeviceViewModel.setIsApSSIDSameAsDNS(isChecked)
        }

        binding.apPasswordInputEditTextConnectDeviceWifiFragment.doOnTextChanged { text, _, _, _ ->
            addDeviceViewModel.setAPPassword(text.toString())
        }
        binding.staSSIDInputEditTextNetworkConfigFragment.doOnTextChanged { text, _, _, _ ->
            addDeviceViewModel.setStaSSID(text.toString())
        }
        binding.staPasswordInputEditTextConnectDeviceWifiFragment.doOnTextChanged { text, _, _, _ ->
            addDeviceViewModel.setStaPassword(text.toString())
        }
        binding.autoConnectCheckBoxNetworkConfigFragment.setOnCheckedChangeListener { _, isChecked ->
            addDeviceViewModel.setAutoConnect(isChecked)
        }
        binding.apSSIDCheckBoxNetworkConfigFragment.isChecked = addDeviceViewModel.networkConfig.value?.isApSSIDSameAsDNS ?: true
        binding.autoConnectCheckBoxNetworkConfigFragment.isChecked = addDeviceViewModel.networkConfig.value?.isAutoConnect ?: true
        binding.configureButtonNetworkConfigFragment.setOnClickListener {
            addDeviceViewModel.configureNetwork()
        }
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

}