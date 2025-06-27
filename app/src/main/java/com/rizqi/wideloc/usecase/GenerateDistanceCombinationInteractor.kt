package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.Distance
import com.rizqi.wideloc.domain.model.Point
import com.rizqi.wideloc.domain.model.Variable
import kotlin.random.Random

class GenerateDistanceCombinationInteractor : GenerateDistanceCombinationUseCase{

    override fun invoke(devices: List<DeviceData>): List<Distance> {
        val distances = mutableListOf<Distance>()

        if (devices.size < 2) return emptyList()

        for (i in 0 until devices.size-1){
            for (j in i + 1 until devices.size){
                val device1 = devices[i]
                val device2 = devices[j]

                val x1 = Random.nextDouble(from = -10.0, until = 10.0)
                val y1 = Random.nextDouble(from = -10.0, until = 10.0)
                val point1 = Point(
                    id = device1.getCorrespondingPointId(),
                    x = Variable(device1.getCorrespondingXId(), x1),
                    y = Variable(device1.getCorrespondingYId(), y1)
                )

                val x2 = Random.nextDouble(from = -10.0, until = 10.0)
                val y2 = Random.nextDouble(from = -10.0, until = 10.0)
                val point2 = Point(
                    id = device2.getCorrespondingPointId(),
                    x = Variable(device2.getCorrespondingXId(), x2),
                    y = Variable(device2.getCorrespondingYId(), y2)
                )

                distances.add(
                    Distance(
                        id = GenerateIDInteractor().invoke(),
                        point1 = point1,
                        point2 = point2,
                        distance = euclideanDistance(point1, point2),
                        timestamp = 0
                )
                )
            }
        }
        return distances
    }

    private fun euclideanDistance(p1: Point, p2: Point): Double {
        val dx = p1.x.value - p2.x.value
        val dy = p1.y.value - p2.y.value
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}