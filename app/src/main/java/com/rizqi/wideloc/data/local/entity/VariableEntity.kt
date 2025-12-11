package com.rizqi.wideloc.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single variable used in point calculations.
 *
 * A variable typically corresponds to a coordinate component (e.g., X or Y),
 * and stores both an identifier and a numeric value. These variables are used
 * when computing 2D/3D positions within a UWB tracking session.
 *
 * @property id Unique identifier for this variable.
 * @property value The numeric value associated with this variable.
 */
data class VariableEntity(
    val id: String,
    val value: Double,
)

/**
 * Represents a single point computed during a UWB tracking session.
 *
 * Each point stores its X and Y coordinate components as embedded
 * [VariableEntity] objects. The variables are embedded with distinct prefixes
 * to avoid column name collisions inside the Room table.
 *
 * This entity is typically used for storing the estimated position of
 * a device at a given moment in the session.
 *
 * @property id Unique identifier for the point.
 * @property x X-axis coordinate stored as an embedded variable.
 * @property y Y-axis coordinate stored as an embedded variable.
 */
@Entity(tableName = "points")
data class PointEntity(
    @PrimaryKey val id: String,
    @Embedded(prefix = "x_") val x: VariableEntity,
    @Embedded(prefix = "y_") val y: VariableEntity,
)
