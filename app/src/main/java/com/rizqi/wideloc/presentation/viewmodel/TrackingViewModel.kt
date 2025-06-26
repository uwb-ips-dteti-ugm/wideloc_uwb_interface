package com.rizqi.wideloc.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqi.wideloc.data.local.entity.DeviceRole
import com.rizqi.wideloc.domain.model.DeviceTrackingHistoryData
import com.rizqi.wideloc.domain.model.Point
import com.rizqi.wideloc.domain.model.Variable
import com.rizqi.wideloc.domain.repository.DeviceRepository
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

    private val _sessionId = MutableLiveData<String>()
    val sessionId: LiveData<String> get() = _sessionId

    private val _deviceTrackingHistoryFlow = MutableStateFlow<List<DeviceTrackingHistoryData>?>(null)
    val deviceTrackingHistoryFlow: StateFlow<List<DeviceTrackingHistoryData>?> get() = _deviceTrackingHistoryFlow

    init {
        _sessionId.value = generateIDUseCase.invoke()

        viewModelScope.launch {
            val devices = deviceRepository.getAllDevices().first { it.isNotEmpty() }
            val data = devices.map {
                DeviceTrackingHistoryData(
                    deviceData = it,
                    points = listOf(
                        Point(
                            id = it.getCorrespondingPointId(),
                            x = Variable(it.getCorrespondingXId(), 0.0),
                            y = Variable(it.getCorrespondingYId(), 0.0)
                        )
                    ),
                    distances = listOf(),
                    timestamp = 0
                )
            }
            _deviceTrackingHistoryFlow.value = data
        }

    }

    fun startObserveData() {
        viewModelScope.launch {
            val data = _deviceTrackingHistoryFlow.value ?: return@launch
            val server = deviceRepository.getFirstByRole(DeviceRole.Server)
            val anchors = deviceRepository.getByRole(DeviceRole.Anchor)
            if (server == null || anchors.isEmpty()) return@launch

            while (true) {
                getUpdatedPositionUseCase.invoke(
                    sessionId = 1,
                    server = server,
                    anchors = anchors,
                    deviceTrackingHistories = data
                )
            }
        }
    }
}