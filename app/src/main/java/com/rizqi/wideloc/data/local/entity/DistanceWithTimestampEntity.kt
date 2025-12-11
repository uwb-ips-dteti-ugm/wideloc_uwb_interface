package com.rizqi.wideloc.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a batch of UWB distance measurements recorded at a specific moment
 * within a tracking session.
 *
 * Each entry in this table acts as a grouping container for multiple
 * [DistanceEntity] records that share the same timestamp. This enables the
 * system to associate all distances measured at the same iteration or scan
 * cycle with a single parent entity.
 *
 * The parent-child relationship ensures that when a tracking session is deleted,
 * all associated distance batches and their distances are removed automatically.
 *
 * @property id Auto-generated unique identifier for this distance batch.
 * @property sessionId ID of the tracking session this batch belongs to.
 * @property timestamp Timestamp (epoch milliseconds) representing when this
 * distance batch was recorded.
 */
@Entity(
    tableName = "distances_with_timestamp",
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
data class DistancesWithTimestampEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val timestamp: Long,
)
