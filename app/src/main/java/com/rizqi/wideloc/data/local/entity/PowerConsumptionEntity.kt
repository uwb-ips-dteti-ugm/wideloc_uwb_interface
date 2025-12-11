package com.rizqi.wideloc.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents recorded power consumption data for a specific tracking session.
 *
 * This entity stores detailed metrics related to device energy usage during
 * a UWB tracking process. The information is useful for evaluating device
 * efficiency, battery drain patterns, and the overall energy cost of ranging
 * operations.
 *
 * @property id Auto-generated unique identifier for this power consumption record.
 * @property sessionId ID of the associated tracking session.
 * @property powerMilliWatts Total measured power consumption in milliwatts (mW).
 * @property currentMicroAmps Electric current draw measured in microamperes (µA).
 * @property startBatteryLevel Battery level (%) at the start of the measurement period.
 * @property endBatteryLevel Battery level (%) at the end of the measurement period.
 * @property batteryDrop The difference between start and end battery levels.
 * @property durationInMilliSeconds Total measurement duration in milliseconds.
 * @property timestamp Unix timestamp marking when the measurement was recorded.
 */
@Entity(
    tableName = "power_consumptions",
    foreignKeys = [
        ForeignKey(
            entity = TrackingSessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class PowerConsumptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val powerMilliWatts: Double,
    val currentMicroAmps: Long,
    val startBatteryLevel: Int,
    val endBatteryLevel: Int,
    val batteryDrop: Int,
    val durationInMilliSeconds: Double,
    val timestamp: Long,
)
