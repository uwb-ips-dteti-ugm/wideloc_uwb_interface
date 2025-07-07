package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.databinding.FragmentSelectDevicesBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.ConnectViaWiFiFragment
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking.adapters.SelectClientListAdapter
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking.adapters.SelectDeviceListAdapter
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus

class SelectDevicesFragment :
    BaseFragment<FragmentSelectDevicesBinding>(FragmentSelectDevicesBinding::inflate) {

    private val trackingViewModel: TrackingViewModel by activityViewModels()

    private lateinit var serverDeviceListAdapter: SelectDeviceListAdapter
    private lateinit var anchorDeviceListAdapter: SelectDeviceListAdapter
    private lateinit var clientDeviceListAdapter: SelectClientListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serverDeviceListAdapter = SelectDeviceListAdapter {
            trackingViewModel.setSelectedServer(it)
        }
        anchorDeviceListAdapter = SelectDeviceListAdapter {
            trackingViewModel.setSelectedAnchor(it)
        }
        clientDeviceListAdapter = SelectClientListAdapter {

        }

        (parentFragment?.parentFragment as? SetupTrackingSessionBottomSheet)?.toggleWifiInfoVisibility(false)

        binding.serverRecyclerViewSelectDevicesFragment.adapter = serverDeviceListAdapter
        binding.serverRecyclerViewSelectDevicesFragment.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.anchorRecyclerViewSelectDevicesFragment.adapter = anchorDeviceListAdapter
        binding.anchorRecyclerViewSelectDevicesFragment.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.clientsRecyclerViewSelectDevicesFragment.adapter = clientDeviceListAdapter
        binding.clientsRecyclerViewSelectDevicesFragment.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.selectAllTextViewSelectDevicesFragment.setOnClickListener {
            clientDeviceListAdapter.selectAll()
        }
        binding.connectButtonSelectDevicesFragment.setOnClickListener {
            val selectedClients = clientDeviceListAdapter.getSelectedDevices()
            trackingViewModel.setSelectedClients(selectedClients)
            trackingViewModel.validateSelectedDevices()
        }
        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus(requireActivity().currentFocus ?: it)
        }

        trackingViewModel.serverList.observe(viewLifecycleOwner){servers ->
            serverDeviceListAdapter.submitList(servers)
        }
        trackingViewModel.anchorList.observe(viewLifecycleOwner){anchors ->
            anchorDeviceListAdapter.submitList(anchors)
        }
        trackingViewModel.clientList.observe(viewLifecycleOwner){clients ->
            clientDeviceListAdapter.submitList(clients)
        }
        trackingViewModel.selectDevicesResult.observe(viewLifecycleOwner){result ->
            when(result){
                is Result.Error -> Toast.makeText(requireContext(), result.errorMessage, Toast.LENGTH_LONG).show()
                is Result.Loading<*> -> Unit
                is Result.Success<*> -> (parentFragment as? SetupTrackingSessionFragment)?.goToNextPage()
            }
        }

        recalculateContentHeight()
    }

    private fun recalculateContentHeight() {
        view?.post {
            (parentFragment?.parentFragment as? SetupTrackingSessionBottomSheet)?.recalculateHeight(
                listOf(
                    (parentFragment as? SetupTrackingSessionFragment)?.binding?.stepsIndicatorFragmentSetupTrackingSession,
                    binding.root,
                ),
            )
        }
    }

}