package com.rizqi.wideloc.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
