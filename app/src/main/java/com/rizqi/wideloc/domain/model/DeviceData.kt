package com.rizqi.wideloc.domain.model

import java.time.LocalDateTime
import java.util.Date

data class DeviceData(
    val id: String,
    val name: String,
    val imageUrl: String,
    val role: String,
    val offset: DeviceOffsetData,
    val protocol: ProtocolData,
    val isAvailable: Boolean,
    val lastConnectedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
)
