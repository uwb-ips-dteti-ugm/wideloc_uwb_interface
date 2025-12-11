package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an indoor map used within the WideLoc system.
 *
 * A map typically corresponds to a physical environment such as a room,
 * laboratory, hallway, or building floor where UWB tracking is performed.
 * The map can store a visual reference (e.g., floor layout image) that is
 * used by the UI to render anchor positions, device movements, or tracking paths.
 *
 * @property id Auto-generated unique identifier for this map.
 * @property name Display name of the map (e.g., "Lab A", "Indoor Arena Map").
 * @property imageUri URI or file path pointing to the map image resource stored locally.
 */
@Entity(tableName = "maps")
data class MapEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("image_uri")
    val imageUri: String,
)
