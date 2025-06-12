package com.rizqi.wideloc.domain.model

open class ProtocolData

data class WifiProtocolData(
    val port: Int,
    val mdns: String,
    val autoConnect: Boolean,
    val deviceAccessPointSSID: String,
    val deviceAccessPointPPassword: String,
    val networkSSID: String,
    val networkPassword: String,
) : ProtocolData()

data class BluetoothProtocolData(
    val hostId: String,
    val hostAddress: String,
) : ProtocolData()
