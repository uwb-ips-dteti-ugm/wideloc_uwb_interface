package com.rizqi.wideloc.data.local.entity

/**
 * Represents the positional offset of a device relative to its physical mounting point.
 *
 * Device offsets are commonly used in UWB positioning systems to correct measurements
 * when the UWB module is not located exactly at the geometric center of the device
 * or tag.
 *
 * Offsets ensure that distance calculations and trilateration results remain accurate
 * by compensating for physical displacement in 3D space.
 *
 * @property x Offset along the X-axis (in meters).
 * @property y Offset along the Y-axis (in meters).
 * @property z Offset along the Z-axis (in meters).
 */
data class DeviceOffsetEntity(
    val x: Double,
    val y: Double,
    val z: Double,
)
