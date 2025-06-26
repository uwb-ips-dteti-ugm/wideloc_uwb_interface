package com.rizqi.wideloc.domain.repository

import com.rizqi.wideloc.domain.model.ClientData
import com.rizqi.wideloc.domain.model.TWRData
import com.rizqi.wideloc.domain.model.UWBConfigData
import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.model.WifiConnectData

interface UWBDeviceRepository {
    suspend fun getClientInfo(dns: String): List<ClientData>

    suspend fun getTWRData(dns: String): List<TWRData>

    suspend fun connectWifi(wifiConnectData: WifiConnectData): Boolean

    suspend fun disconnectWifi(dns: String): Boolean

    suspend fun configWifi(wifiConfigData: WifiConfigData): Boolean

    suspend fun configUWB(dns: String, uwbConfigData: UWBConfigData): Boolean

    suspend fun restartDevice(dns: String): Boolean
}