package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.DeviceTrackingHistoryData
import com.rizqi.wideloc.domain.model.Distance

interface GetUpdatedPositionUseCase {
    suspend fun invoke(
        sessionId: Int,
        server: DeviceData,
        anchors: List<DeviceData>,
        deviceTrackingHistories: List<DeviceTrackingHistoryData>
    ) : List<DeviceTrackingHistoryData>
}