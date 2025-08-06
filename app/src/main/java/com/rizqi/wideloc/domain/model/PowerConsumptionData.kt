package com.rizqi.wideloc.domain.model

data class PowerConsumptionData(
    val powerMilliWatts: Double,
    val currentMicroAmps: Long,
    val startBatteryLevel: Int,
    val endBatteryLevel: Int,
    val batteryDrop: Int,
    val durationInMilliSeconds: Double,
    val timestamp: Long,
)
