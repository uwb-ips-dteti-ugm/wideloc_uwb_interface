package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

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

enum class UWBMode(val mode: Int){
    None(0),
    TDOA(1),
    TWR(2)
}
