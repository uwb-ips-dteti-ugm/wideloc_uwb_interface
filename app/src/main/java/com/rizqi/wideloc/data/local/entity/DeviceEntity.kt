package com.rizqi.wideloc.data.local.entity

import androidx.room.*
import java.time.LocalDateTime

/**
 * Represents a UWB-related device stored in the local Room database.
 *
 * Each device may represent an Anchor, Tag, or any unit involved in
 * the WideLoc indoor positioning system.
 *
 * The entity stores device metadata, protocol configurations,
 * UWB configuration, connectivity details, and timestamps.
 *
 * This model is central to device management, configuration generation,
 * and session initialization in the WideLoc system.
 *
 * @property id Unique identifier of the device.
 * @property name Human-readable name of the device.
 * @property imageUrl Optional image URL representing the device visually.
 * @property role The role of the device (e.g., Anchor, Tag).
 * @property offset Positional offset configuration for UWB trilateration.
 * @property protocol Communication protocol used by this device (Wi-Fi, Bluetooth, UWB).
 * @property wiFiProtocolEntity Wi-Fi protocol metadata if the device uses Wi-Fi communication.
 * @property bluetoothProtocolEntity Bluetooth protocol metadata if Bluetooth communication is used.
 * @property uwbConfigEntity UWB-specific configuration data (device address, network address, etc.).
 * @property isAvailable Whether the device is currently available/connected.
 * @property lastConnectedAt Timestamp of the most recent successful connection.
 * @property createdAt Timestamp when the device was created and stored in the database.
 */
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

    @Embedded(prefix = "wifi_protocol_")
    val wiFiProtocolEntity: WiFiProtocolEntity? = null,

    @Embedded(prefix = "bluetooth_")
    val bluetoothProtocolEntity: BluetoothProtocolEntity? = null,

    @Embedded(prefix = "uwb_config_")
    val uwbConfigEntity: UWBConfigEntity? = null,

    @ColumnInfo(name = "is_available")
    val isAvailable: Boolean = false,

    @ColumnInfo(name = "last_connected_at")
    val lastConnectedAt: LocalDateTime? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
)
