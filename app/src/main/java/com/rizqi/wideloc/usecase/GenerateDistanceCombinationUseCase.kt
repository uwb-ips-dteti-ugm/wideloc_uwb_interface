package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.Distance
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel

interface GenerateDistanceCombinationUseCase {
    fun invoke(
        devices: List<DeviceData>,
        layoutInitialCoordinate: TrackingViewModel.LayoutInitialCoordinate,
    ): List<Distance>
}