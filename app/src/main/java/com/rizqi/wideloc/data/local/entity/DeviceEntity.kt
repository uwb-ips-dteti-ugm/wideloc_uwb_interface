package com.rizqi.wideloc.data.local.entity

import androidx.room.*
import java.time.LocalDateTime
import java.util.Date

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
    val bluetoothProtocolEntity: BluetoothProtocolEntity? = null,

    @ColumnInfo(name = "is_available")
    val isAvailable: Boolean = false,

    @ColumnInfo(name = "last_connected_at")
    val lastConnectedAt: LocalDateTime? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    )
