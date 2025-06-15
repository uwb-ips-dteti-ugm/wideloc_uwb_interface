package com.rizqi.wideloc.utils

import com.google.gson.Gson
import com.rizqi.wideloc.data.local.entity.BluetoothProtocolEntity
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.DeviceOffsetEntity
import com.rizqi.wideloc.data.local.entity.DeviceProtocol
import com.rizqi.wideloc.data.local.entity.DeviceProtocol.*
import com.rizqi.wideloc.data.local.entity.UWBConfigEntity
import com.rizqi.wideloc.data.local.entity.WiFiProtocolEntity
import com.rizqi.wideloc.data.network.dto.WifiConfigDto
import com.rizqi.wideloc.data.network.dto.WifiConnectDto
import com.rizqi.wideloc.data.websocket.request.CalibrationRequest
import com.rizqi.wideloc.data.websocket.response.TrackingResponse
import com.rizqi.wideloc.domain.model.BluetoothProtocolData
import com.rizqi.wideloc.domain.model.CalibrationData
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.DeviceOffsetData
import com.rizqi.wideloc.domain.model.ProtocolData
import com.rizqi.wideloc.domain.model.TrackingData
import com.rizqi.wideloc.domain.model.UWBConfigData
import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.model.WifiConnectData
import com.rizqi.wideloc.domain.model.WifiProtocolData
import com.rizqi.wideloc.utils.DomainDataMapper.asProtocolData

object DomainDataMapper {
    fun mapTrackingResponseTextToTrackingData(
        input: String
    ): TrackingData? {
        return try {
            val response = Gson().fromJson(input, TrackingResponse::class.java)
            with(response) {
                TrackingData(
                    x = x,
                    y = y,
                    z = z,
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun CalibrationData.asCalibrationRequest(): CalibrationRequest {
        return CalibrationRequest(data = data)
    }

    fun DeviceEntity.asDeviceData(): DeviceData {
        return DeviceData(
            id = this.id,
            name = this.name,
            imageUrl = this.imageUrl,
            role = this.role,
            offset = this.offset.asDeviceOffsetData(),
            protocol = when (this.protocol) {
                WiFi -> this.wiFiProtocolEntity.asProtocolData()
                Bluetooth -> this.bluetoothProtocolEntity.asProtocolData()
            },
            isAvailable = this.isAvailable,
            lastConnectedAt = this.lastConnectedAt,
            createdAt = this.createdAt,
            uwbConfigData = this.uwbConfigEntity?.asUWBConfigData(),
        )
    }

    fun DeviceOffsetEntity.asDeviceOffsetData(): DeviceOffsetData {
        return DeviceOffsetData(x = this.x, y = this.y, z = this.z)
    }

    fun WiFiProtocolEntity?.asProtocolData(): ProtocolData =
        WifiProtocolData(
            port = this?.port ?: 80,
            mdns = this?.mdns ?: "esp32-uwb",
            autoConnect = this?.autoConnect ?:true,
            deviceAccessPointSSID = this?.deviceAccessPointSSID ?: "esp32-uwb",
            deviceAccessPointPPassword = this?.deviceAccessPointPPassword ?: "12345678",
            networkSSID = this?.networkSSID ?: "",
            networkPassword = this?.networkPassword ?: "",
        )

    fun BluetoothProtocolEntity?.asProtocolData(): ProtocolData = BluetoothProtocolData(
        hostId = this?.hostId ?: "",
        hostAddress = this?.hostAddress ?: "",
    )

    fun DeviceData.asDeviceEntity(): DeviceEntity {
        return DeviceEntity(
            id = this.id,
            name = this.name,
            imageUrl = this.imageUrl,
            role = this.role,
            offset = this.offset.asDeviceOffsetEntity(),
            protocol = when (protocol) {
                is WifiProtocolData -> WiFi
                is BluetoothProtocolData -> Bluetooth
                else -> WiFi
            },
            wiFiProtocolEntity = this.protocol.asWifiProtocolEntity(),
            bluetoothProtocolEntity = this.protocol.asBluetoothProtocolEntity(),
            isAvailable = this.isAvailable,
            lastConnectedAt = this.lastConnectedAt,
            createdAt = this.createdAt,
            uwbConfigEntity = this.uwbConfigData?.asUWBConfigEntity(),
        )
    }

    fun DeviceOffsetData.asDeviceOffsetEntity(): DeviceOffsetEntity = DeviceOffsetEntity(
        x = this.x,
        y = this.y,
        z = this.z,
    )

    fun ProtocolData.asWifiProtocolEntity(): WiFiProtocolEntity? {
        if (this is WifiProtocolData) return WiFiProtocolEntity(
            port = this.port,
            mdns = this.mdns,
            autoConnect = this.autoConnect,
            deviceAccessPointSSID = this.deviceAccessPointSSID,
            deviceAccessPointPPassword = this.deviceAccessPointPPassword,
            networkSSID = this.networkSSID,
            networkPassword = this.networkPassword,
        )
        return null
    }

    fun ProtocolData.asBluetoothProtocolEntity(): BluetoothProtocolEntity? {
        if (this is BluetoothProtocolData) return BluetoothProtocolEntity(
            hostId = this.hostId,
            hostAddress = this.hostAddress,
        )
        return null
    }
    
    fun UWBConfigEntity.asUWBConfigData() = UWBConfigData(
        autoStart = this.autoStart,
        isServer = this.isServer,
        maxClient = this.maxClient,
        mode = this.mode,
        networkAddress = this.networkAddress,
        deviceAddress = this.deviceAddress,
    )

    fun UWBConfigData.asUWBConfigEntity() = UWBConfigEntity(
        autoStart = this.autoStart,
        isServer = this.isServer,
        maxClient = this.maxClient,
        mode = this.mode,
        networkAddress = this.networkAddress,
        deviceAddress = this.deviceAddress,
    )

    fun WifiConfigData.toDto() = WifiConfigDto(
        port = this.port,
        mdns = this.mdns,
    )

    fun WifiConnectData.toDto() = WifiConnectDto(
        autoConnect = this.autoConnect,
        apSSID = this.apSSID,
        apPassword = this.apPassword,
        staSSID = this.staSSID,
        staPassword = this.staPassword,
    )
}