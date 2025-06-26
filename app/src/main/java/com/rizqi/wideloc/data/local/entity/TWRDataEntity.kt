package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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

