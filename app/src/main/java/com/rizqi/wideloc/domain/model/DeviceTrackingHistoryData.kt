package com.rizqi.wideloc.domain.model

data class DeviceTrackingHistoryData(
    val deviceData: DeviceData,
    val points: List<Point>,
    val distances: List<Distance>,
    val timestamp: Long,
)
