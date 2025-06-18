package com.rizqi.wideloc.presentation.ui.connect_via_wifi

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.databinding.FragmentNetworkConfigBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceViewModel
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus
import java.net.HttpURLConnection
import java.net.URL

class NetworkConfigFragment : BaseFragment<FragmentNetworkConfigBinding>(FragmentNetworkConfigBinding::inflate) {

    private val addDeviceViewModel: AddDeviceViewModel by activityViewModels()

    private lateinit var connectivityManager: ConnectivityManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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
        addDeviceViewModel.configNetworkResult.observe(viewLifecycleOwner) { result ->
            when(result){
                is Result.Error -> toggleButtonAndProgressIndicator(false)
                is Result.Loading<*> -> toggleButtonAndProgressIndicator(true)
                is Result.Success<*> -> toggleButtonAndProgressIndicator(false)
                null -> toggleButtonAndProgressIndicator(false)
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
        binding.dnsInputLayoutNetworkConfigFragment.prefixText = addDeviceViewModel.getNamePrefix()
        binding.apSSIDInputLayoutNetworkConfigFragment.prefixText = addDeviceViewModel.getNamePrefix()
    }

    override fun onDestroyView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.bindProcessToNetwork(null)
        }
        super.onDestroyView()
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

    private fun toggleButtonAndProgressIndicator(isLoading: Boolean){
        binding.configureButtonNetworkConfigFragment.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressIndicatorNetworkConfigFragment.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}