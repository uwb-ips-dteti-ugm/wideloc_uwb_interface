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
    private val person2 = Pair(1.0, 0.0)

    private val person3Positions = listOf(
        Pair(0.0, 1.0), Pair(0.5, 1.0), Pair(1.0, 1.0), Pair(1.5, 1.0), Pair(2.0, 1.0),
        Pair(2.0, 0.0), Pair(1.5, 0.0), Pair(1.0, 0.0), Pair(0.5, 0.0), Pair(0.0, 0.0),
        Pair(0.0, 0.5), Pair(0.0, 1.0)
    )

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