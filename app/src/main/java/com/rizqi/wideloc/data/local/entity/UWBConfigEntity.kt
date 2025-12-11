package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

/**
 * Represents the UWB (Ultra-Wideband) configuration assigned to a specific device.
 *
 * This configuration defines how the device behaves in a UWB network,
 * including its addressing, operational mode, and whether it acts as a server.
 *
 * The fields are also annotated for Gson serialization to support
 * communication with external hardware or microcontrollers.
 *
 * @property autoStart Whether the UWB module should automatically start when powered on.
 * @property isServer Indicates whether this device serves as the UWB server in the network.
 * @property maxClient Maximum number of clients supported (used in server mode).
 * @property mode The UWB operational mode, such as TDOA or TWR.
 * @property networkAddress The device’s assigned network address.
 * @property deviceAddress The device’s unique UWB hardware address.
 */
data class UWBConfigEntity(
    @SerializedName("autostart")
    @ColumnInfo(name = "auto_start")
    val autoStart: Boolean,

    @SerializedName("is_server")
    @ColumnInfo(name = "is_server")
    val isServer: Boolean,

    @SerializedName("client_max")
    @ColumnInfo(name = "max_client")
    val maxClient: Int,

    @SerializedName("mode")
    @ColumnInfo(name = "mode")
    val mode: UWBMode,

    @SerializedName("network_addr")
    @ColumnInfo(name = "network_address")
    val networkAddress: Int,

    @SerializedName("device_addr")
    @ColumnInfo(name = "device_address")
    val deviceAddress: Int,
)

/**
 * Defines the available UWB operating modes supported by WideLoc.
 *
 * @property mode Numeric identifier used for communication with UWB hardware.
 */
enum class UWBMode(val mode: Int) {
    /** No UWB mode configured. */
    None(0),

    /** Time Difference of Arrival (TDOA) mode — requires multiple anchors. */
    TDOA(1),

    /** Two-Way Ranging (TWR) mode — computes distances via request-response. */
    TWR(2)
}
