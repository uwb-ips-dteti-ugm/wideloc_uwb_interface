package com.rizqi.wideloc.domain.model

import java.time.LocalDateTime

data class TrackingSessionData(
    val sessionId: Int = 0,
    val date: LocalDateTime = LocalDateTime.now(),
    val recordedDistances: MutableList<DistancesWithTimestamp> = mutableListOf(),
    var deviceTrackingHistoryData: MutableList<DeviceTrackingHistoryData> = mutableListOf(),
    var latencies: MutableList<LatencyData> = mutableListOf()
)
