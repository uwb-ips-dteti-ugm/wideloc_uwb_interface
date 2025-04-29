package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo

data class WiFiProtocolEntity(
    @ColumnInfo(name = "socket_url")
    val socketUrl: String
)
