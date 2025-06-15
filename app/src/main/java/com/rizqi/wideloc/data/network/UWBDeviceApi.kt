package com.rizqi.wideloc.data.network

import com.rizqi.wideloc.data.network.dto.ClientInfoDto
import com.rizqi.wideloc.data.network.dto.SuccessDto
import com.rizqi.wideloc.data.network.dto.TWRDto
import com.rizqi.wideloc.data.network.dto.WifiConfigDto
import com.rizqi.wideloc.data.network.dto.WifiConnectDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UWBDeviceApi {
    @GET("api/uwb/client/info")
    suspend fun getClientInfo(): ClientInfoDto

    @GET("api/uwb/client/twr")
    suspend fun getTWRData(): TWRDto

    @POST("api/wifi/connect")
    suspend fun connectWifi(@Body wifiConnectDto: WifiConnectDto): SuccessDto

    @POST("api/wifi/disconnect")
    suspend fun disconnectWifi(): SuccessDto

    @POST("api/wifi/config")
    suspend fun configWifi(@Body wifiConfigDto: WifiConfigDto): SuccessDto

    @POST("api/uwb/config")
    suspend fun configUWB(): SuccessDto

    @POST("api/device/restart")
    suspend fun restartDevice(): SuccessDto
}