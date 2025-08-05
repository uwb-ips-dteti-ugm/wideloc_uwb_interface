package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.LatencyData

class CalculateLatencyInteractor : CalculateLatencyUseCase {

    override fun invoke(startTime: Long, endTime: Long): LatencyData {
        val latency = (endTime - startTime).toDouble() / 1_000_000

        return LatencyData(
            timestamp = System.currentTimeMillis(),
            latency = latency
        )
    }
}