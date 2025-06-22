package com.rizqi.wideloc.domain.repository

import com.rizqi.wideloc.data.network.dto.TWRDto
import com.rizqi.wideloc.domain.model.ClientData
import com.rizqi.wideloc.domain.model.UWBConfigData
import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.model.WifiConnectData

interface UWBDeviceRepository {
    suspend fun getClientInfo(): List<ClientData>

    suspend fun getTWRData(): List<TWRDto>

    suspend fun connectWifi(wifiConnectData: WifiConnectData): Boolean

    suspend fun disconnectWifi(): Boolean

    suspend fun configWifi(wifiConfigData: WifiConfigData): Boolean

    suspend fun configUWB(dns: String, uwbConfigData: UWBConfigData): Boolean

    suspend fun restartDevice(): Boolean
}