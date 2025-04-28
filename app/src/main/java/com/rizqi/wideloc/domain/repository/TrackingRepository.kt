package com.rizqi.wideloc.domain.repository

import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.domain.model.CalibrationData
import com.rizqi.wideloc.domain.model.TrackingData
import kotlinx.coroutines.flow.Flow

interface TrackingRepository {
    fun observeWebSocket(): Flow<Result<String>>

    fun observeInspection(): Flow<TrackingData>

    suspend fun sendCalibration(calibrationData: CalibrationData)
}