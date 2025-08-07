package com.rizqi.wideloc.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
