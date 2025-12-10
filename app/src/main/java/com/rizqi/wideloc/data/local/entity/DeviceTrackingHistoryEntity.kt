package com.rizqi.wideloc.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents the historical tracking record of a device during a specific
 * UWB tracking session.
 *
 * Each entry stores a timestamp associated with a device, allowing the system
 * to reconstruct movement timelines, analyze positioning performance, or match
 * raw UWB ranging data to individual devices.
 *
 * This entity is linked to a [TrackingSessionEntity], and all records are
 * automatically deleted when the session is removed.
 *
 * @property id Auto-generated unique identifier for the history entry.
 * @property sessionId The ID of the tracking session this history belongs to.
 * @property deviceId The device identifier (foreign key to DeviceEntity.id).
 * @property timestamp Timestamp representing when the device record was captured,
 *                     stored as epoch milliseconds.
 */
@Entity(
    tableName = "device_tracking_history",
    foreignKeys = [
        ForeignKey(
            entity = TrackingSessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("deviceId")]
)
data class DeviceTrackingHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val deviceId: String,  // FK to DeviceEntity.id
    val timestamp: Long,
)
