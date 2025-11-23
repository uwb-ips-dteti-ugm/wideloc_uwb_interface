package com.rizqi.wideloc.domain.model

import com.rizqi.wideloc.domain.model.MapUnit.*
import com.rizqi.wideloc.utils.fromMeters
import java.time.LocalDateTime

data class TrackingSessionData(
    val sessionId: Int = 0,
    val date: LocalDateTime = LocalDateTime.now(),
    val recordedDistances: MutableList<DistancesWithTimestamp> = mutableListOf(),
    var deviceTrackingHistoryData: MutableList<DeviceTrackingHistoryData> = mutableListOf(),
    var latencies: MutableList<LatencyData> = mutableListOf(),
    var powerConsumptions: MutableList<PowerConsumptionData> = mutableListOf(),
    var elapsedTime: Long = 0,
) {
    fun getUnitTransformedDeviceTrackingHistoryData(mapUnit: MapUnit): MutableList<DeviceTrackingHistoryData> {
        return deviceTrackingHistoryData.map { historyData ->

            val transformedPoints = historyData.points.map { point ->
                val newX = fromMeters(point.x.value, mapUnit)
                val newY = fromMeters(point.y.value, mapUnit)

                point.copyWithNewCoordinate(newX, newY)
            }

            historyData.copy(points = transformedPoints)
        }.toMutableList()
    }

}
