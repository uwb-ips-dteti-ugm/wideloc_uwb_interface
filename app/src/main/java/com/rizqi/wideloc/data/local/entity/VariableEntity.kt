package com.rizqi.wideloc.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

data class VariableEntity(
    val id: String,
    val value: Double,
)

@Entity(tableName = "points")
data class PointEntity(
    @PrimaryKey val id: String,
    @Embedded(prefix = "x_") val x: VariableEntity,
    @Embedded(prefix = "y_") val y: VariableEntity,
)
