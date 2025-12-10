package com.rizqi.wideloc.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a single measured distance between two UWB points during a
 * specific measurement batch (group).
 *
 * Each record corresponds to a distance calculation performed by the UWB
 * ranging system, typically representing the distance between two anchors,
 * or between an anchor and a tag.
 *
 * Distances are grouped using [groupId], which links this entity to a
 * corresponding [DistancesWithTimestampEntity] entry containing shared metadata
 * (e.g., batch timestamp).
 *
 * When a distance group is deleted, all associated distances are automatically removed.
 *
 * @property id Unique identifier for this distance record.
 * @property groupId Identifier referencing the distance batch group.
 * @property point1Id ID of the first UWB device or anchor involved in the measurement.
 * @property point2Id ID of the second UWB device or anchor involved in the measurement.
 * @property distance Measured distance (in meters) between the two points.
 * @property timestamp Timestamp of the distance measurement in epoch milliseconds.
 */
@Entity(
    tableName = "distances",
    foreignKeys = [
        ForeignKey(
            entity = DistancesWithTimestampEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId")]
)
data class DistanceEntity(
    @PrimaryKey val id: String,
    val groupId: Int,
    val point1Id: String,
    val point2Id: String,
    val distance: Double,
    val timestamp: Long
)
