package com.rizqi.wideloc.data.local.entity

import androidx.room.*
import java.time.LocalDateTime

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: String,
    val name: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String,

    @ColumnInfo(name = "role")
    val role: DeviceRole,

    @Embedded(prefix = "offset_")
    val offset: DeviceOffsetEntity,

    @ColumnInfo(name = "protocol")
    val protocol: DeviceProtocol,

    @Embedded(prefix = "wifi_")
    val wiFiProtocolEntity: WiFiProtocolEntity? = null,

    @Embedded(prefix = "bluetooth_")
    val bluetoothProtocolEntity: BluetoothProtocolEntity? = null,

    @ColumnInfo(name = "uwb_config_entity")
    val uwbConfigEntity : UWBConfigEntity? = null,

    @ColumnInfo(name = "is_available")
    val isAvailable: Boolean = false,

    @ColumnInfo(name = "last_connected_at")
    val lastConnectedAt: LocalDateTime? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    )
