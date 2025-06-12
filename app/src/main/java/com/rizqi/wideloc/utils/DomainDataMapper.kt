package com.rizqi.wideloc.utils

import com.google.gson.Gson
import com.rizqi.wideloc.data.local.entity.BluetoothProtocolEntity
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.DeviceOffsetEntity
import com.rizqi.wideloc.data.local.entity.DeviceProtocol
import com.rizqi.wideloc.data.local.entity.DeviceProtocol.*
import com.rizqi.wideloc.data.local.entity.WiFiProtocolEntity
import com.rizqi.wideloc.data.websocket.request.CalibrationRequest
import com.rizqi.wideloc.data.websocket.response.TrackingResponse
import com.rizqi.wideloc.domain.model.BluetoothProtocolData
import com.rizqi.wideloc.domain.model.CalibrationData
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.DeviceOffsetData
import com.rizqi.wideloc.domain.model.ProtocolData
import com.rizqi.wideloc.domain.model.TrackingData
import com.rizqi.wideloc.domain.model.WifiProtocolData

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
        )
    }

    fun DeviceOffsetEntity.asDeviceOffsetData(): DeviceOffsetData {
        return DeviceOffsetData(x = this.x, y = this.y, z = this.z)
    }

    fun WiFiProtocolEntity?.asProtocolData(): ProtocolData =
        WifiProtocolData(socketUrl = this?.socketUrl ?: "")

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
        )
    }

    fun DeviceOffsetData.asDeviceOffsetEntity(): DeviceOffsetEntity = DeviceOffsetEntity(
        x = this.x,
        y = this.y,
        z = this.z,
    )

    fun ProtocolData.asWifiProtocolEntity(): WiFiProtocolEntity? {
        if (this is WifiProtocolData) return WiFiProtocolEntity(socketUrl = this.socketUrl)
        return null
    }

    fun ProtocolData.asBluetoothProtocolEntity(): BluetoothProtocolEntity? {
        if (this is BluetoothProtocolData) return BluetoothProtocolEntity(
            hostId = this.hostId,
            hostAddress = this.hostAddress,
        )
        return null
    }
}