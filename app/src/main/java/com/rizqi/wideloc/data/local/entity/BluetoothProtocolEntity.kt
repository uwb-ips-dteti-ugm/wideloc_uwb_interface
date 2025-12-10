package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo

/**
 * Represents metadata required for Bluetooth communication within the WideLoc system.
 *
 * This entity stores identifying information for the Bluetooth host device,
 * such as its MAC address and a logical host ID.
 *
 * Although not a Room table by itself (no @Entity annotation),
 * this model is typically embedded or used as part of other entities that
 * require Bluetooth protocol configuration.
 *
 * @property hostAddress The MAC address of the Bluetooth host device.
 * @property hostId A logical identifier representing the Bluetooth host within the system.
 */
data class BluetoothProtocolEntity(
    @ColumnInfo(name = "host_address")
    val hostAddress: String,

    @ColumnInfo(name = "host_id")
    val hostId: String
)
