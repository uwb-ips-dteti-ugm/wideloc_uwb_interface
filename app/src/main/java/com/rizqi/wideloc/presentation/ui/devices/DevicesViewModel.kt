package com.rizqi.wideloc.presentation.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.usecase.DeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val deviceUseCase: DeviceUseCase,
) : ViewModel() {
    val availableDevices: StateFlow<List<DeviceData>> =
        deviceUseCase.getAvailableDevices()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val reconfigureDevices: StateFlow<List<DeviceData>> =
        deviceUseCase.getReconfigureDevices()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

}