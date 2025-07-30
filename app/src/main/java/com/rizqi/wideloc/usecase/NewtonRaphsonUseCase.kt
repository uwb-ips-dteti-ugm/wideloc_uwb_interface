package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.Distance
import com.rizqi.wideloc.domain.model.Point

interface NewtonRaphsonUseCase {
    suspend fun f(distance: Distance): Double

    suspend fun createF(distances: List<Distance>): Map<String, Double>

    suspend fun calculateJacobian(
        distances: List<Distance>,
        variableIds: List<String>,
        h: Double = 1e-5
    ): Map<String, Map<String, Double>>

    suspend fun calculateDelta(
        jacobian: Map<String, Map<String, Double>>,
        fMap: Map<String, Double>
    ): Map<String, Double>

    suspend fun newtonRaphson(
        initialDistances: List<Distance>,
        fixedPointIds: Set<String>,
        iteration: Int = 10,
        tolerance: Double = 1e-10,
        h: Double = 1e-4
    ): List<Point>

    suspend fun centralDifference(
        base: Distance,
        update: (Distance, Double) -> Distance,
        f: suspend (Distance) -> Double,
        h: Double
    ): Double

}