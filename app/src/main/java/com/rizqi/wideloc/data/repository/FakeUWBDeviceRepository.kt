package com.rizqi.wideloc.data.repository

import com.rizqi.wideloc.domain.model.ClientData
import com.rizqi.wideloc.domain.model.TWRData
import com.rizqi.wideloc.domain.model.UWBConfigData
import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.model.WifiConnectData
import com.rizqi.wideloc.domain.repository.UWBDeviceRepository
import kotlin.math.sqrt

class FakeUWBDeviceRepository : UWBDeviceRepository {

    private val person1 = Pair(0.0, 0.0)
    private val person2 = Pair(0.3, 0.0)

    private val person3Positions = buildRoute(
        listOf(
            0.0 to 0.3,
            0.0 to 0.4,
            0.2 to 0.4,
            0.2 to 0.0,
            0.0 to 0.0,
            0.0 to 0.4
        ),
        step = 0.1
    )

    private fun buildRoute(points: List<Pair<Double, Double>>, step: Double): List<Pair<Double, Double>> {
        val result = mutableListOf<Pair<Double, Double>>()
        for (i in 0 until points.lastIndex) {
            val (x0, y0) = points[i]
            val (x1, y1) = points[i + 1]

            val dx = x1 - x0
            val dy = y1 - y0
            val distance = kotlin.math.hypot(dx, dy)
            val steps = (distance / step).toInt()

            for (j in 0 until steps) {
                val t = j * step / distance
                val x = x0 + dx * t
                val y = y0 + dy * t
                result.add(Pair(x, y))
            }
        }

        result.add(points.last())

        return result
    }

    private var currentStep = 0

    override suspend fun getClientInfo(dns: String): List<ClientData> = emptyList()

    override suspend fun getTWRData(dns: String): List<TWRData> {
        val index = currentStep % person3Positions.size
        val position3 = person3Positions[index]

        val timestamp = (System.currentTimeMillis() / 1000).toInt()

        val d13 = distance(person1, position3)
        val d23 = distance(person2, position3)
        val d12 = distance(person1, person2) // static

        currentStep++ // Advance to next position for next call

        return listOf(
            TWRData(
                address1 = 1,
                address2 = 3,
                timestamp = timestamp,
                distance = d13
            ),
            TWRData(
                address1 = 2,
                address2 = 3,
                timestamp = timestamp,
                distance = d23
            ),
            TWRData(
                address1 = 1,
                address2 = 2,
                timestamp = timestamp,
                distance = d12
            )
        )
    }

    private fun distance(p1: Pair<Double, Double>, p2: Pair<Double, Double>): Double {
        val dx = p1.first - p2.first
        val dy = p1.second - p2.second
        return sqrt(dx * dx + dy * dy)
    }

    override suspend fun connectWifi(wifiConnectData: WifiConnectData): Boolean = false

    override suspend fun disconnectWifi(dns: String): Boolean = false

    override suspend fun configWifi(wifiConfigData: WifiConfigData): Boolean = false

    override suspend fun configUWB(dns: String, uwbConfigData: UWBConfigData): Boolean = false

    override suspend fun restartDevice(dns: String): Boolean = false
}