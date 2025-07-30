package com.rizqi.wideloc.domain.model

import java.time.LocalDateTime

data class TrackingSessionData(
    val sessionId: Int = 0,
    val date: LocalDateTime = LocalDateTime.now(),
    val recordedDistances: List<DistancesWithTimestamp> = listOf(),
    val deviceTrackingHistoryData: List<DeviceTrackingHistoryData> = listOf(),
)
