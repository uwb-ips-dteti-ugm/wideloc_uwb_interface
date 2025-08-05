package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.LatencyData

interface CalculateLatencyUseCase {
    fun invoke(startTime: Long, endTime: Long): LatencyData
}
