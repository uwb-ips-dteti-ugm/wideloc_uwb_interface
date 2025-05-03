package com.rizqi.wideloc.domain.model

data class DeviceData(
    val id: String,
    val name: String,
    val imageUrl: String,
    val role: String,
    val offset: DeviceOffsetData,
    val protocol: ProtocolData
)
