package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single Two-Way Ranging (TWR) measurement record.
 *
 * This entity stores raw UWB measurement data produced during
 * distance calculations between two devices. Each record includes
 * device addresses involved, timestamp, and the computed distance.
 *
 * TWR data may optionally be associated with a tracking session.
 *
 * @property id Auto-generated unique identifier for this TWR data entry.
 * @property sessionId Optional ID of the tracking session this data belongs to.
 * @property address1 The first UWB device address involved in the TWR exchange.
 * @property address2 The second UWB device address involved in the TWR exchange.
 * @property timestamp Unix timestamp (milliseconds) when the measurement occurred.
 * @property distance Calculated distance between the two devices in meters.
 */
@Entity(tableName = "twr_data")
data class TWRDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "session_id")
    val sessionId: Int? = null,

    @ColumnInfo(name = "address_1")
    val address1: Int,

    @ColumnInfo(name = "address_2")
    val address2: Int,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "distance")
    val distance: Double
)
