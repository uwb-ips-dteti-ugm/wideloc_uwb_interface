package com.rizqi.wideloc.presentation.ui.connect_via_wifi

import android.R
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.data.local.entity.DeviceRole
import com.rizqi.wideloc.databinding.FragmentDeviceConfigBinding
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceViewModel
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus
import kotlin.math.max

class DeviceConfigFragment : BaseFragment<FragmentDeviceConfigBinding>(FragmentDeviceConfigBinding::inflate) {

    private val addDeviceViewModel: AddDeviceViewModel by activityViewModels()
    private lateinit var serversAdapter: ArrayAdapter<String>
    private var availableServers: List<DeviceData> = listOf()
    private var selectedServer: DeviceData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (parentFragment?.parentFragment as? AddDeviceBottomSheet)?.toggleWifiInfoVisibility(true)
        recalculateContentHeight()

        addDeviceViewModel.deviceSetupModel.observe(viewLifecycleOwner){deviceSetup ->
            binding.serverLayoutDeviceConfigFragment.visibility = if (deviceSetup.role == DeviceRole.Server) View.VISIBLE else View.GONE
            binding.clientLayoutDeviceConfigFragment.visibility = if (deviceSetup.role == DeviceRole.Server) View.GONE else View.VISIBLE
        }
        addDeviceViewModel.uwbConfigError.observe(viewLifecycleOwner){error ->
            binding.maxClientInputLayoutDeviceConfigFragment.error = error?.client
            binding.networkAddressInputLayoutDeviceConfigFragment.error = error?.networkAddress
            binding.deviceAddressInputLayoutDeviceConfigFragment.error = error?.deviceAddress
            binding.selectServerInputLayoutDeviceConfigFragment.error = error?.server
        }
        addDeviceViewModel.availableServers.observe(viewLifecycleOwner){servers ->
            availableServers = servers
            serversAdapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_dropdown_item_1line,
                availableServers.map { "${it.id}-${it.name}" }
            )
            binding.selectServerAutoCompleteDeviceConfigFragment.apply {
                setAdapter(serversAdapter)
                setText(selectedServer?.name, false)
                setOnItemClickListener { _, _, position, _ ->
                    val server = availableServers[position]
                    selectedServer = server
                    addDeviceViewModel.setServer(server)
                }
                setOnClickListener {
                    binding.selectServerAutoCompleteDeviceConfigFragment.showDropDown()
                    recalculateContentHeight()
                }
            }
        }
        addDeviceViewModel.getNetworkAddress {
            binding.networkAddressInputEditTextDeviceConfigFragment.setText(it.toString())
        }
        addDeviceViewModel.getDeviceAddress {
            binding.deviceAddressInputEditTextDeviceConfigFragment.setText(it.toString())
        }
        addDeviceViewModel.configUWBResult.observe(viewLifecycleOwner){ result ->
            when(result){
                is Result.Error -> toggleButtonAndProgressIndicator(false)
                is Result.Loading<*> -> toggleButtonAndProgressIndicator(true)
                is Result.Success<*> -> {
                    toggleButtonAndProgressIndicator(false)
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
//                    (parentFragment as? ConnectViaWiFiFragment)?.goToNextPage()
                }
                null -> toggleButtonAndProgressIndicator(false)
            }
        }

        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus(requireActivity().currentFocus ?: it)
        }
        binding.configureButtonDeviceConfigFragment.setOnClickListener {
            val maxClient = binding.maxClientInputEditTextDeviceConfigFragment.text.toString()
            val networkAddress = binding.networkAddressInputEditTextDeviceConfigFragment.text.toString()
            val deviceAddress = binding.deviceAddressInputEditTextDeviceConfigFragment.text.toString()
            val isAutoStart = binding.autoStartCheckBoxDeviceConfigFragment.isChecked

            addDeviceViewModel.configureDevice(
                maxClient = maxClient,
                networkAddress = networkAddress,
                deviceAddress = deviceAddress,
                isAutoStart = isAutoStart,
            )
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

    private fun toggleButtonAndProgressIndicator(isLoading: Boolean){
        binding.configureButtonDeviceConfigFragment.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressIndicatorDeviceConfigFragment.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}