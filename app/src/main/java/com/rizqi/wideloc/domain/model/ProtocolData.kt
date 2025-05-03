package com.rizqi.wideloc.domain.model

open class ProtocolData

data class WifiProtocolData(
    val socketUrl: String,
) : ProtocolData()

data class BluetoothProtocolData(
    val hostId: String,
    val hostAddress: String,
) : ProtocolData()
