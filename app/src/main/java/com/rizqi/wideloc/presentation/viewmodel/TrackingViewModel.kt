package com.rizqi.wideloc.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.data.local.entity.DeviceRole
import com.rizqi.wideloc.domain.model.DeviceTrackingHistoryData
import com.rizqi.wideloc.domain.model.DistancesWithTimestamp
import com.rizqi.wideloc.domain.model.Point
import com.rizqi.wideloc.domain.model.TrackingSessionData
import com.rizqi.wideloc.domain.model.Variable
import com.rizqi.wideloc.domain.repository.DeviceRepository
import com.rizqi.wideloc.usecase.GenerateDistanceCombinationInteractor
import com.rizqi.wideloc.usecase.GenerateDistanceCombinationUseCase
import com.rizqi.wideloc.usecase.GenerateIDInteractor
import com.rizqi.wideloc.usecase.GenerateIDUseCase
import com.rizqi.wideloc.usecase.GetUpdatedPositionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val getUpdatedPositionUseCase: GetUpdatedPositionUseCase,
    private val deviceRepository: DeviceRepository,
) : ViewModel() {

    private val generateIDUseCase: GenerateIDUseCase = GenerateIDInteractor()
    private val generateDistanceCombination: GenerateDistanceCombinationUseCase =
        GenerateDistanceCombinationInteractor()

    private val _session = MutableLiveData<TrackingSessionData>()
    val session: LiveData<TrackingSessionData> get() = _session

    private val _observeResult = MutableLiveData<Result<TrackingSessionData?>?>(null)
    val observeResult: LiveData<Result<TrackingSessionData?>?> = _observeResult

    init {
        viewModelScope.launch {
            val devices = deviceRepository.getAllDevices().first { it.isNotEmpty() }
            val distances = generateDistanceCombination.invoke(devices)
            val deviceTrackingHistories = devices.map { device ->
                DeviceTrackingHistoryData(
                    deviceData = device,
                    points = listOf(
                        Point(
                            id = device.getCorrespondingPointId(),
                            x = Variable(
                                id = device.getCorrespondingXId(),
                                value = 0.0
                            ),
                            y = Variable(
                                id = device.getCorrespondingYId(),
                                value = 0.0
                            ),
                        )
                    ),
                    timestamp = 0
                )
            }
            _session.value = TrackingSessionData(
                sessionId = 0,
                recordedDistances = listOf(
                    DistancesWithTimestamp(
                        timestamp = 0,
                        distances = distances
                    )
                ),
                deviceTrackingHistoryData = deviceTrackingHistories
            )
        }

    }

    fun startObserveData() {
        viewModelScope.launch {
//            _observeResult.value = Result.Loading()
            val session = session.value ?: return@launch
            val server = deviceRepository.getFirstByRole(DeviceRole.Server)
            val anchors = deviceRepository.getByRole(DeviceRole.Anchor)
            if (server == null || anchors.isEmpty()) return@launch
//            try {
                val updatedSession = getUpdatedPositionUseCase.invoke(
                    session = session,
                    server = server,
                    anchors = anchors
                )
                _session.value = updatedSession
//                _observeResult.value = Result.Success(updatedSession)
//            } catch (e: Exception){
//                _observeResult.value = Result.Error(e.message.toString())
//            }
        }
    }
}