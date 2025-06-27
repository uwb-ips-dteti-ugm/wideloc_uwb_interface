package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.Distance

interface GenerateDistanceCombinationUseCase {
    fun invoke(devices: List<DeviceData>): List<Distance>
}