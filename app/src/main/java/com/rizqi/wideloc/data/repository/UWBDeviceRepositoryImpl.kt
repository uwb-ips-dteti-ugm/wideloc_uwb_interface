package com.rizqi.wideloc.data.repository

import com.rizqi.wideloc.data.network.UWBDeviceApi
import com.rizqi.wideloc.data.network.dto.TWRDto
import com.rizqi.wideloc.domain.model.ClientData
import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.model.WifiConnectData
import com.rizqi.wideloc.domain.repository.UWBDeviceRepository
import com.rizqi.wideloc.utils.DomainDataMapper.toDto
import javax.inject.Inject

class UWBDeviceRepositoryImpl @Inject constructor(
    private val uwbDeviceApi: UWBDeviceApi,
) : UWBDeviceRepository {
    override suspend fun getClientInfo(): List<ClientData> {
        return listOf()
    }

    override suspend fun getTWRData(): List<TWRDto> {
       return listOf()
    }

    override suspend fun connectWifi(wifiConnectData: WifiConnectData): Boolean {
        val dto = wifiConnectData.toDto()
        uwbDeviceApi.connectWifi(dto)
        return true
    }

    override suspend fun disconnectWifi(): Boolean {
        return true
    }

    override suspend fun configWifi(wifiConfigData: WifiConfigData): Boolean {
        val dto = wifiConfigData.toDto()
        uwbDeviceApi.configWifi(dto)
        return true
    }

    override suspend fun configUWB(): Boolean {
        return true
    }

    override suspend fun restartDevice(): Boolean {
        return true
    }
}