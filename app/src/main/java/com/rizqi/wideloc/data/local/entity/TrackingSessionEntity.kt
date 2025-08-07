package com.rizqi.wideloc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tracking_sessions")
data class TrackingSessionEntity(
    @PrimaryKey(autoGenerate = true) val sessionId: Int = 0,
    val date: LocalDateTime,
    val elapsedTime: Long,
)

