package com.rizqi.wideloc.domain.model

data class MapTransform(
    val length: Double = 6.0,
    val width: Double = 4.0,
    val unit: MapUnit = MapUnit.CM,
    val axisStep: Double = 0.5,
    val rotation: Float = 0f,
    val isFlipX: Boolean = false,
)