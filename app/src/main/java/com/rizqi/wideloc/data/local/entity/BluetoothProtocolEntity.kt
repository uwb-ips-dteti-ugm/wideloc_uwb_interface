package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo

data class BluetoothProtocolEntity(
    @ColumnInfo(name = "host_address")
    val hostAddress: String,

    @ColumnInfo(name = "host_id")
    val hostId: String
)
