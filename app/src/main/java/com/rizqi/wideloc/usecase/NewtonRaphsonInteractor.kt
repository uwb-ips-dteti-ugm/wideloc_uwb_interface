package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.Distance
import com.rizqi.wideloc.domain.model.Point
import com.rizqi.wideloc.utils.MatrixUtils.invertMatrix
import com.rizqi.wideloc.utils.MatrixUtils.multiply2DMatrix
import com.rizqi.wideloc.utils.MatrixUtils.multiplyMatrix
import com.rizqi.wideloc.utils.MatrixUtils.transposeMatrix
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.pow

class NewtonRaphsonInteractor : NewtonRaphsonUseCase {

    private val TAG = "NewtonRaphsonInteractor"

    override suspend fun f(distance: Distance): Double {
        val x1 = distance.point1.x.value
        val x2 = distance.point2.x.value
        val y1 = distance.point1.y.value
        val y2 = distance.point2.y.value
        val d = distance.distance

        return (x2 - x1).pow(2.0) + (y2 - y1).pow(2.0) - d.pow(2.0)
    }

    override suspend fun createF(distances: List<Distance>): Map<String, Double> {
        return distances.associate { it.id to f(it) }
    }

    override suspend fun calculateJacobian(
        distances: List<Distance>,
        variableIds: List<String>,
        h: Double
    ): Map<String, Map<String, Double>> {
        val jacobian = mutableMapOf<String, MutableMap<String, Double>>()

        for (distance in distances) {
            val row = mutableMapOf<String, Double>()

            for (varId in variableIds) {
                val value = when (varId) {
                    distance.point1.x.id -> centralDifference(
                        base = distance,
                        update = { d, delta -> d.withPoint1X(d.point1.x.value + delta) },
                        f = ::f,
                        h = h
                    )
                    distance.point2.x.id -> centralDifference(
                        base = distance,
                        update = { d, delta -> d.withPoint2X(d.point2.x.value + delta) },
                        f = ::f,
                        h = h
                    )
                    distance.point1.y.id -> centralDifference(
                        base = distance,
                        update = { d, delta -> d.withPoint1Y(d.point1.y.value + delta) },
                        f = ::f,
                        h = h
                    )
                    distance.point2.y.id -> centralDifference(
                        base = distance,
                        update = { d, delta -> d.withPoint2Y(d.point2.y.value + delta) },
                        f = ::f,
                        h = h
                    )
                    else -> 0.0
                }
                row[varId] = value
            }

            jacobian[distance.id] = row
        }

        return jacobian
    }

    override suspend fun calculateDelta(
        jacobian: Map<String, Map<String, Double>>,
        fMap: Map<String, Double>
    ): Map<String, Double> {
        val distanceIds = jacobian.keys.toList()
        val variableIds = jacobian.values.firstOrNull()?.keys?.toList() ?: emptyList()

        // Build jacobian matrix and f vector in correct order
        val jacobianMatrix = distanceIds.map { distId ->
            variableIds.map { varId ->
                jacobian[distId]?.get(varId) ?: 0.0
            }
        }
        val fVector = distanceIds.map { fMap[it] ?: 0.0 }

        // 1. transposeMatrix of Jacobian (n x m)
        val jT = transposeMatrix(jacobianMatrix)

        // 2. Jᵗ * J  => (n x n)
        val jtJ = multiply2DMatrix(jT, jacobianMatrix)

        // 3. Invert (Jᵗ * J)
        val jtJInv = invertMatrix(jtJ)

        // 4. Jᵗ * F  => (n x 1)
        val jtF = multiplyMatrix(jT, fVector)

        // 5. ΔX = - (Jᵗ * J)⁻¹ * (Jᵗ * F)
        val delta = multiplyMatrix(jtJInv, jtF).map { -it }

        val deltaMap = variableIds.zip(delta).toMap()

        deltaMap.forEach { (id, value) -> println("  $id: $value") }

        return deltaMap
    }

    override suspend fun newtonRaphson(
        initialDistances: List<Distance>,
        fixedPointIds: Set<String>,
        iteration: Int,
        tolerance: Double,
        h: Double,
    ): List<Point> {
        var distances = initialDistances.toList()

        val points = distances
            .flatMap { listOf(it.point1, it.point2) }
            .distinctBy { it.id }
            .associateBy { it.id }
            .toMutableMap()

        val allVariables = distances.flatMap { d ->
            listOf(d.point1.x, d.point2.x, d.point1.y, d.point2.y)
        }.distinctBy { it.id }
            .associateBy { it.id }
            .toMutableMap()

        // Take all the dynamic points
        val variableIds = allVariables
            .filter { (id, variable) ->
                val parentPoint = points.values.find { it.x.id == id || it.y.id == id }
                parentPoint != null && parentPoint.id !in fixedPointIds
            }
            .map { it.key }

        repeat(iteration) { iter ->
            val fMatrix = createF(distances)
            val jacobian = calculateJacobian(distances, variableIds, h)
            val delta = calculateDelta(jacobian, fMatrix)

            if (delta.size != variableIds.size) {
                Timber.i("Delta size (${delta.size}) does not match variable size (${variableIds.size})")
                return@repeat
            }

            val maxDelta = delta.values.maxOf { abs(it) }
            if (maxDelta < tolerance) {
                return points.values.toList()
            }

            // Update only unfixed variables
            variableIds.forEach { varId ->
                val variable = allVariables[varId] ?: return@forEach
                val delta = delta[varId] ?: 0.0
                allVariables[varId] = variable.copy(value = variable.value + delta)
            }

            // Update points with new variables
            points.values.forEach { point ->
                val updatedX = allVariables[point.x.id] ?: point.x
                val updatedY = allVariables[point.y.id] ?: point.y
                points[point.id] = point.copy(x = updatedX, y = updatedY)
            }

            // Update distances
            distances = distances.map { distance ->
                distance.copy(
                    point1 = points[distance.point1.id] ?: distance.point1,
                    point2 = points[distance.point2.id] ?: distance.point2
                )
            }
        }

        return points.values.toList()
    }

    override suspend fun centralDifference(
        base: Distance,
        update: (Distance, Double) -> Distance,
        f: suspend (Distance) -> Double,
        h: Double
    ) : Double {
        val f1 = f(update(base, -h / 2))
        val f2 = f(update(base, +h / 2))
        return (f2 - f1) / h
    }
}