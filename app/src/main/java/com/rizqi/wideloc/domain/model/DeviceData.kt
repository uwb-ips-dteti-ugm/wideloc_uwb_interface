package com.rizqi.wideloc.domain.model

import com.rizqi.wideloc.data.local.entity.DeviceRole
import java.time.LocalDateTime

data class DeviceData(
    val id: String,
    val name: String,
    val imageUrl: String,
    val role: DeviceRole,
    val offset: DeviceOffsetData,
    val protocol: ProtocolData,
    val uwbConfigData: UWBConfigData?,
    val isAvailable: Boolean,
    val lastConnectedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    fun getCorrespondingPointId() = "$id.point"
    fun getCorrespondingXId() = "$id.point.x"
    fun getCorrespondingYId() = "$id.point.y"
}
