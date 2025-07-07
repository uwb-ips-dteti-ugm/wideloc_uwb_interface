package com.rizqi.wideloc.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maps")
data class MapEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("image_uri")
    val imageUri: String,
)
