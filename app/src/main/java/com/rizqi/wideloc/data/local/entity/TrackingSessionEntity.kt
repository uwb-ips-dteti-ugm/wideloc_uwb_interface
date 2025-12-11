package com.rizqi.wideloc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Represents a single UWB tracking session recorded by the WideLoc system.
 *
 * Each session groups together distance measurements, latency records,
 * device tracking histories, power consumption data, and computed points.
 * This entity acts as the root parent for many other tracking-related tables.
 *
 * @property sessionId Auto-generated unique identifier for the tracking session.
 * @property date The date and time when the tracking session was started.
 * @property elapsedTime Total duration of the session in milliseconds.
 */
@Entity(tableName = "tracking_sessions")
data class TrackingSessionEntity(
    @PrimaryKey(autoGenerate = true) val sessionId: Int = 0,
    val date: LocalDateTime,
    val elapsedTime: Long,
)
