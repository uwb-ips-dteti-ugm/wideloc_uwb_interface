package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.Distance
import com.rizqi.wideloc.domain.model.Point
import com.rizqi.wideloc.domain.model.Variable
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import kotlin.math.sqrt

class GenerateDistanceCombinationInteractor : GenerateDistanceCombinationUseCase {

    override fun invoke(
        devices: List<DeviceData>,
        layoutInitialCoordinate: TrackingViewModel.LayoutInitialCoordinate
    ): List<Distance> {
        if (devices.size < 2) return emptyList()
        return devices.indices.flatMap { i ->
            (i + 1 until devices.size).mapNotNull { j ->
                val d1 = devices[i]
                val d2 = devices[j]
                val p1 = toPoint(d1, layoutInitialCoordinate)
                val p2 = toPoint(d2, layoutInitialCoordinate)
                Distance(
                    id = GenerateIDInteractor().invoke(),
                    point1 = p1,
                    point2 = p2,
                    distance = euclideanDistance(p1, p2),
                    timestamp = 0
                )
            }
        }
    }

    private fun toPoint(device: DeviceData, layout: TrackingViewModel.LayoutInitialCoordinate): Point {
        val coord = when (device.id) {
            layout.serverCoordinate.deviceData?.id -> layout.serverCoordinate.coordinate
            layout.anchorCoordinate.deviceData?.id -> layout.anchorCoordinate.coordinate
            else -> layout.clientsCoordinate.find { it.deviceData?.id == device.id }?.coordinate
                ?: TrackingViewModel.Coordinate()
        }
        return Point(
            id = device.getCorrespondingPointId(),
            x = Variable(device.getCorrespondingXId(), coord.x),
            y = Variable(device.getCorrespondingYId(), coord.y)
        )
    }

    private fun euclideanDistance(p1: Point, p2: Point): Double {
        val dx = p1.x.value - p2.x.value
        val dy = p1.y.value - p2.y.value
        return sqrt(dx * dx + dy * dy)
    }
}
