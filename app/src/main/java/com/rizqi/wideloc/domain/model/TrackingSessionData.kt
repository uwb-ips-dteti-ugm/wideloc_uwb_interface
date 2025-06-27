package com.rizqi.wideloc.domain.model

data class TrackingSessionData(
    val sessionId: Int,
    val recordedDistances: List<DistancesWithTimestamp>,
    val deviceTrackingHistoryData: List<DeviceTrackingHistoryData>,
)
