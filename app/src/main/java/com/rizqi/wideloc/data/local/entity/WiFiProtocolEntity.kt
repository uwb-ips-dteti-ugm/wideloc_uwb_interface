package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo

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
