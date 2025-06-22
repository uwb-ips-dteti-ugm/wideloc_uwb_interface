package com.rizqi.wideloc.data.repository

import android.util.Log
import com.google.gson.Gson
import com.rizqi.wideloc.data.network.HTTPApiClient
import com.rizqi.wideloc.data.network.UWBDeviceApi
import com.rizqi.wideloc.data.network.dto.TWRDto
import com.rizqi.wideloc.domain.model.ClientData
import com.rizqi.wideloc.domain.model.UWBConfigData
import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.model.WifiConnectData
import com.rizqi.wideloc.domain.repository.UWBDeviceRepository
import com.rizqi.wideloc.utils.Constants
import com.rizqi.wideloc.utils.DomainDataMapper.asUWBConfigEntity
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
        val jsonBody = Gson().toJson(wifiConnectData.toDto())
        HTTPApiClient().post(Constants.WIFI_CONNECT_ENDPOINT, jsonBody)
        return true
    }

    override suspend fun disconnectWifi(): Boolean {
        return true
    }

    override suspend fun configWifi(wifiConfigData: WifiConfigData): Boolean {
        val jsonBody = Gson().toJson(wifiConfigData.toDto())
        HTTPApiClient().post(Constants.WIFI_CONFIG_ENDPOINT, jsonBody)
        return true
    }

    override suspend fun configUWB(dns: String, uwbConfigData: UWBConfigData): Boolean {
        val jsonBody = Gson().toJson(uwbConfigData.asUWBConfigEntity())
        HTTPApiClient(dns).post(Constants.UWB_CONFIG_ENDPOINT, jsonBody)
        return true
    }

    override suspend fun restartDevice(): Boolean {
        return true
    }
}