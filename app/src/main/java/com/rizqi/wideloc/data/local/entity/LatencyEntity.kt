package com.rizqi.wideloc.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a latency measurement recorded during a tracking session.
 *
 * This entity stores the time delay (in milliseconds) observed during a
 * positioning or communication cycle in the UWB system. Latency data is useful
 * for performance monitoring, debugging, and evaluating the responsiveness of
 * the tracking pipeline.
 *
 * Each latency record is associated with a specific tracking session and will be
 * automatically removed if the parent session is deleted.
 *
 * @property id Auto-generated unique identifier for this latency entry.
 * @property sessionId ID of the tracking session to which this latency value belongs.
 * @property timestamp Timestamp (epoch milliseconds) indicating when the latency
 * measurement was captured.
 * @property latency Measured latency value in milliseconds.
 */
@Entity(
    tableName = "latencies",
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
data class LatencyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val timestamp: Long,
    val latency: Double,
)
