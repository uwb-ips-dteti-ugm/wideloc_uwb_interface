package com.rizqi.wideloc.data.repository

import com.rizqi.wideloc.domain.model.ClientData
import com.rizqi.wideloc.domain.model.TWRData
import com.rizqi.wideloc.domain.model.UWBConfigData
import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.model.WifiConnectData
import com.rizqi.wideloc.domain.repository.UWBDeviceRepository
import kotlin.random.Random

class FakeUWBDeviceRepository  : UWBDeviceRepository {
    override suspend fun getClientInfo(dns: String): List<ClientData> = emptyList()

    override suspend fun getTWRData(dns: String): List<TWRData> {
        return listOf(
            TWRData(
                address1 = 1,
                address2 = 2,
                timestamp = (System.currentTimeMillis() / 1000).toInt(),
//                distance = Random.nextDouble(0.0, 10.0)
                distance = 1.1
            ),
            TWRData(
                address1 = 1,
                address2 = 3,
                timestamp = (System.currentTimeMillis() / 1000).toInt(),
                distance = 1.1
            ),
            TWRData(
                address1 = 2,
                address2 = 3,
                timestamp = (System.currentTimeMillis() / 1000).toInt(),
                distance = 1.1
            )
        )
    }

    override suspend fun connectWifi(wifiConnectData: WifiConnectData): Boolean = false

    override suspend fun disconnectWifi(dns: String): Boolean = false

    override suspend fun configWifi(wifiConfigData: WifiConfigData): Boolean = false

    override suspend fun configUWB(dns: String, uwbConfigData: UWBConfigData): Boolean = false

    override suspend fun restartDevice(dns: String): Boolean = false

}