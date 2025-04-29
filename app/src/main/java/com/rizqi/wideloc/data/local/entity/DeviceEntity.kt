package com.rizqi.wideloc.data.local.entity

import androidx.room.*

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: String,
    val name: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String,

    val role: String,

    @Embedded(prefix = "offset_")
    val offset: DeviceOffsetEntity,

    @ColumnInfo(name = "protocol")
    val protocol: DeviceProtocol,

    @Embedded(prefix = "wifi_")
    val wiFiProtocolEntity: WiFiProtocolEntity? = null,

    @Embedded(prefix = "bluetooth_")
    val bluetoothProtocolEntity: BluetoothProtocolEntity? = null
)
