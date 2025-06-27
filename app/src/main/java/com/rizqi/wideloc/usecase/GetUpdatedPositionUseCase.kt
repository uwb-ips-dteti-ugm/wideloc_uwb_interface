package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.TrackingSessionData

interface GetUpdatedPositionUseCase {
    suspend fun invoke(
        session: TrackingSessionData,
        server: DeviceData,
        anchors: List<DeviceData>,
    ) : TrackingSessionData
}