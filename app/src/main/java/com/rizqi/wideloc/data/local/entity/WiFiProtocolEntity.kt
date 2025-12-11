package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo

/**
 * Represents the Wi-Fi communication configuration for a device.
 *
 * This entity contains the parameters needed for a device to connect either
 * as an access point (AP mode) or as a station (STA mode), depending on the
 * system’s network setup. It is used when the device operates using Wi-Fi
 * as the communication protocol for UWB data transfer.
 *
 * @property port The port number used for Wi-Fi communication.
 * @property mdns The mDNS service name used for device discovery.
 * @property autoConnect Whether the device should automatically connect to the network.
 * @property deviceAccessPointSSID SSID used when the device exposes its own Wi-Fi access point.
 * @property deviceAccessPointPPassword Password for the device’s Wi-Fi access point.
 * @property networkSSID SSID of the external Wi-Fi network that the device should join.
 * @property networkPassword Password for the external Wi-Fi network.
 */
data class WiFiProtocolEntity(
    @ColumnInfo(name = "port")
    val port: Int,

    @ColumnInfo(name = "mdns")
    val mdns: String,

    @ColumnInfo(name = "auto_connect")
    val autoConnect: Boolean,

    @ColumnInfo(name = "device_access_point_ssid")
    val deviceAccessPointSSID: String,

    @ColumnInfo(name = "device_access_point_password")
    val deviceAccessPointPPassword: String,

    @ColumnInfo(name = "network_ssid")
    val networkSSID: String,

    @ColumnInfo(name = "network_password")
    val networkPassword: String,
)
