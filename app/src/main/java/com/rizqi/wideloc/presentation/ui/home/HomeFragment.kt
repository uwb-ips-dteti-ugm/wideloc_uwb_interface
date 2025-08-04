package com.rizqi.wideloc.presentation.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rizqi.wideloc.R
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.databinding.DeviceTagBinding
import com.rizqi.wideloc.databinding.FragmentHomeBinding
import com.rizqi.wideloc.domain.model.Coordinate
import com.rizqi.wideloc.domain.model.DeviceCoordinate
import com.rizqi.wideloc.domain.model.MapTransform
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.setup_tracking.SetupTrackingSessionBottomSheet
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.TrackingStatisticsBottomSheet
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel.RecordingState.*
import com.rizqi.wideloc.utils.toDisplayString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val trackingViewModel: TrackingViewModel by activityViewModels()

    private val setupTrackingSessionBottomSheet = SetupTrackingSessionBottomSheet()
    private val trackingStatisticsBottomSheet = TrackingStatisticsBottomSheet()
    private val deviceTags = mutableListOf<Pair<DeviceCoordinate, DeviceTagBinding>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackingViewModel.observeResult.observe(viewLifecycleOwner){result ->
            when(result){
                is Result.Error -> Toast.makeText(requireContext(), result.errorMessage, Toast.LENGTH_LONG).show()
                is Result.Loading<*> -> Unit
                is Result.Success<*> -> Unit
                null -> Unit
            }
        }
        trackingViewModel.recordingState.observe(viewLifecycleOwner){state ->
            when (state){
                NOT_STARTED -> {
                    binding.startRecordButtonHome.text = getString(R.string.start_recording)
                    binding.stopRecordButtonHome.visibility = View.GONE
                }
                STARTED -> {
                    binding.startRecordButtonHome.text = getString(R.string.pause_recording)
                    binding.stopRecordButtonHome.visibility = View.VISIBLE
                }
                RESUMED -> {
                    binding.startRecordButtonHome.text = getString(R.string.pause_recording)
                    binding.stopRecordButtonHome.visibility = View.VISIBLE
                }
                PAUSED -> {
                    binding.startRecordButtonHome.text = getString(R.string.resume_recording)
                    binding.stopRecordButtonHome.visibility = View.VISIBLE
                }
                END -> {
                    binding.startRecordButtonHome.text = getString(R.string.start_recording)
                    binding.stopRecordButtonHome.visibility = View.GONE
                }
                null -> Unit
            }

        }
        trackingViewModel.session.observe(viewLifecycleOwner){session ->
            if (session == null) return@observe
            val routeMap = session.deviceTrackingHistoryData.associate { device ->
                val route = device.points.map { it.x.value to it.y.value }
                device.deviceData.id to route
            }
            binding.cartesianViewHome.setRoutes(routeMap)

            session.deviceTrackingHistoryData.forEach { deviceTrackingHistoryData ->
                val deviceData = deviceTrackingHistoryData.deviceData
                val latestPoint = deviceTrackingHistoryData.points.lastOrNull()

                if (latestPoint != null){
                    addDeviceTagToCartesianView(
                        DeviceCoordinate(
                            deviceData = deviceData,
                            coordinate = Coordinate(
                                x = latestPoint.x.value,
                                y = latestPoint.y.value
                            )
                        )
                    )
                }

            }
        }
        trackingViewModel.mapTransform.observe(viewLifecycleOwner) {
            binding.cartesianViewHome.post {
                binding.cartesianViewHome.applyMapTransform(
                    it
                )
            }
        }

        binding.startRecordButtonHome.setOnClickListener {
            val recordingState = trackingViewModel.recordingState.value
            when(recordingState){
                NOT_STARTED -> setupTrackingSessionBottomSheet.show(parentFragmentManager, setupTrackingSessionBottomSheet.tag)
                STARTED -> trackingViewModel.pauseObserveTWRData()
                RESUMED -> trackingViewModel.pauseObserveTWRData()
                PAUSED -> trackingViewModel.resumeObserveTWRData()
                END -> Unit
                null -> Unit
            }
        }
        binding.stopRecordButtonHome.setOnClickListener {
            trackingViewModel.stopObserveTWRData()
        }
        binding.cartesianViewHome.post { binding.cartesianViewHome.applyMapTransform(MapTransform()) }
        binding.statisticButtonHome.setOnClickListener {
            trackingStatisticsBottomSheet.show(parentFragmentManager, trackingStatisticsBottomSheet.tag)
        }
        trackingStatisticsBottomSheet.show(parentFragmentManager, trackingStatisticsBottomSheet.tag)
    }

    private fun addDeviceTagToCartesianView(deviceCoordinate: DeviceCoordinate) {
        val tagBinding = DeviceTagBinding.inflate(layoutInflater, null, false)

        val deviceName = deviceCoordinate.deviceData?.name ?: "Unknown"
        val deviceId = deviceCoordinate.deviceData?.id ?: return // ID is required

        val x = deviceCoordinate.coordinate.x
        val y = deviceCoordinate.coordinate.y

        tagBinding.nameTextViewDeviceTag.text = deviceName
        tagBinding.coordinateTextViewDeviceTag.text = getString(
            R.string.coordinate,
            x.toDisplayString(),
            y.toDisplayString()
        )

        // Save for later (if needed)
        deviceTags.add(deviceCoordinate to tagBinding)

        tagBinding.root.scaleX = 0.6f
        tagBinding.root.scaleY = 0.6f

        binding.cartesianViewHome.addOrUpdatePoint(
            id = deviceId,
            view = tagBinding.root,
            x = x,
            y = y
        )
    }

}
