package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo

data class UWBConfigEntity(
    @ColumnInfo(name = "auto_start")
    val autoStart: String,
    @ColumnInfo(name = "is_server")
    val isServer: Boolean,
    @ColumnInfo(name = "max_client")
    val maxClient: Int,
    @ColumnInfo(name = "mode")
    val mode:UWBMode,
    @ColumnInfo(name = "network_address")
    val networkAddress: Int,
    @ColumnInfo(name = "device_address")
    val deviceAddress: Int,
)

enum class UWBMode(val mode: Int){
    None(0),
    TDOA(1),
    TWR(2)
}
