package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.Matrix
import com.rizqi.wideloc.domain.model.Point

class KalmanFilterInteractor(
    private val dt: Double = 0.025,
    private val q: Double = 0.01,
    private val r: Double = 0.1,
    private val p: Double = 500.0
) : KalmanFilterUseCase {

    // State vector: [x, y, vx, vy]
    private var state = Matrix(4, 1)

    // State transition matrix
    private val F = Matrix(arrayOf(
        doubleArrayOf(1.0, 0.0, dt, 0.0),
        doubleArrayOf(0.0, 1.0, 0.0, dt),
        doubleArrayOf(0.0, 0.0, 1.0, 0.0),
        doubleArrayOf(0.0, 0.0, 0.0, 1.0)
    ))

    // Measurement matrix
    private val H = Matrix(arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 1.0, 0.0, 0.0)
    ))

    // Covariance matrix
    private var P = Matrix(4, 4)

    // Process noise covariance
    private val Q = Matrix(4, 4)

    // Measurement noise covariance
    private val R = Matrix(2, 2)

    // Identity matrix
    private val I = Matrix.identity(4)

    private var initialized = false

    private fun initialize(initialMeasurement: Point) {
        // Initialize state with first measurement
        state[0, 0] = initialMeasurement.x.value
        state[1, 0] = initialMeasurement.y.value
        state[2, 0] = 0.0 // vx
        state[3, 0] = 0.0 // vy

        // Initialize covariance matrix with high uncertainty
        P = Matrix.identity(4) * p

        // Initialize process noise covariance
        Q[0, 0] = q
        Q[1, 1] = q
        Q[2, 2] = q
        Q[3, 3] = q

        // Initialize measurement noise covariance
        R[0, 0] = r
        R[1, 1] = r

        initialized = true
    }

    override fun update(measurement: Point): Point {

        if (!initialized){
            initialize(measurement)
        }

        val z = Matrix(arrayOf(
            doubleArrayOf(measurement.x.value),
            doubleArrayOf(measurement.y.value)
        ))

        // Predict step
        val xPred = F * state
        val PPred = F * P * F.transpose() + Q

        // Update step
        val y = z - H * xPred // Innovation
        val S = H * PPred * H.transpose() + R // Innovation covariance
        val K = PPred * H.transpose() * S.inverse() // Kalman gain

        state = xPred + K * y
        P = (I - K * H) * PPred

        return measurement.copyWithNewCoordinate(
            xValue = state[0, 0],
            yValue = state[1, 0]
        )
    }

    data class FilteredPosition(val x: Double, val y: Double, val vx: Double, val vy: Double)

}