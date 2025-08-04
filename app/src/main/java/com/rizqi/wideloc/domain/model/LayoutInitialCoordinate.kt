package com.rizqi.wideloc.domain.model

data class DeviceCoordinate(
    val deviceData: DeviceData? = null,
    val coordinate: Coordinate = Coordinate(),
)

data class LayoutInitialCoordinate(
    val serverCoordinate: DeviceCoordinate = DeviceCoordinate(),
    val anchorCoordinate: DeviceCoordinate = DeviceCoordinate(),
    val mapCoordinate: Coordinate = Coordinate(),
    val clientsCoordinate: List<DeviceCoordinate> = emptyList()
)