package com.rizqi.wideloc.usecase

import android.content.Context
import com.rizqi.wideloc.domain.model.PowerConsumptionData

interface CalculatePowerConsumptionUseCase {
    fun invoke(
        context: Context,
        startTime: Long,
        endTime: Long,
        startBatteryLevel: Int,
        endBatteryLevel: Int
    ): PowerConsumptionData
}
