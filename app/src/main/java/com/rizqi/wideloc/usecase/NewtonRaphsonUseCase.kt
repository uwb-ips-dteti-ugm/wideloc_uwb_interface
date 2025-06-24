package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.Distance

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
    )

    suspend fun newtonRaphson(
        initialDistances: List<Distance>
    )

    fun centralDifference(
        base: Distance,
        update: (Distance, Double) -> Distance,
        f: (Distance) -> Double,
        h: Double
    )

}