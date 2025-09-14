package com.rizqi.wideloc.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.rizqi.wideloc.data.network.HTTPApiClient
import com.rizqi.wideloc.data.network.UWBDeviceApi
import com.rizqi.wideloc.data.network.dto.ClientInfoDto
import com.rizqi.wideloc.data.network.dto.TWRDataDto
import com.rizqi.wideloc.data.network.dto.TWRDto
import com.rizqi.wideloc.domain.model.ClientData
import com.rizqi.wideloc.domain.model.TWRData
import com.rizqi.wideloc.domain.model.UWBConfigData
import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.model.WifiConnectData
import com.rizqi.wideloc.domain.repository.UWBDeviceRepository
import com.rizqi.wideloc.utils.Constants
import com.rizqi.wideloc.utils.DomainDataMapper.asUWBConfigEntity
import com.rizqi.wideloc.utils.DomainDataMapper.toClientData
import com.rizqi.wideloc.utils.DomainDataMapper.toDto
import com.rizqi.wideloc.utils.DomainDataMapper.toTWRData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UWBDeviceRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val uwbDeviceApi: UWBDeviceApi,
) : UWBDeviceRepository {
    override suspend fun getClientInfo(dns: String): List<ClientData> {
        val response = HTTPApiClient(context, dns).get(Constants.UWB_CLIENT_INFO_ENDPOINT)
        val clientInfo = Gson().fromJson(response, ClientInfoDto::class.java)
        return clientInfo.clients.map { it.toClientData() }
    }

    override suspend fun getTWRData(dns: String): List<TWRData> {
        val response = HTTPApiClient(context,"http://172.20.10.3:8080/").get(Constants.UWB_CLIENT_DATA_ENDPOINT)
        val twrDto = Gson().fromJson(response, TWRDto::class.java)
        return twrDto.twrData.map { it.toTWRData() }
    }

    override suspend fun connectWifi(wifiConnectData: WifiConnectData): Boolean {
        val jsonBody = Gson().toJson(wifiConnectData.toDto())
        HTTPApiClient(context).post(Constants.WIFI_CONNECT_ENDPOINT, jsonBody)
        return true
    }

    override suspend fun disconnectWifi(dns: String): Boolean {
        HTTPApiClient(context, dns).post(Constants.WIFI_DISCONNECT_ENDPOINT)
        return true
    }

    override suspend fun configWifi(wifiConfigData: WifiConfigData): Boolean {
        val jsonBody = Gson().toJson(wifiConfigData.toDto())
        HTTPApiClient(context).post(Constants.WIFI_CONFIG_ENDPOINT, jsonBody)
        return true
    }

    override suspend fun configUWB(dns: String, uwbConfigData: UWBConfigData): Boolean {
        val jsonBody = Gson().toJson(uwbConfigData.asUWBConfigEntity())
        HTTPApiClient(context, dns).post(Constants.UWB_CONFIG_ENDPOINT, jsonBody)
        return true
    }

    override suspend fun restartDevice(dns: String): Boolean {
        HTTPApiClient(context, dns).post(Constants.DEVICE_RESTART_ENDPOINT)
        return true
    }
}