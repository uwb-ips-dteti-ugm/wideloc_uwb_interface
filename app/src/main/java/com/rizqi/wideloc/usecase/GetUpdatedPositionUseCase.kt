package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.LayoutInitialCoordinate
import com.rizqi.wideloc.domain.model.MapUnit
import com.rizqi.wideloc.domain.model.TrackingSessionData
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel

interface GetUpdatedPositionUseCase {
    suspend fun invoke(
        session: TrackingSessionData,
        server: DeviceData,
        anchors: List<DeviceData>,
        layoutInitialCoordinate: LayoutInitialCoordinate?,
        mapUnit: MapUnit,
    ) : TrackingSessionData
}