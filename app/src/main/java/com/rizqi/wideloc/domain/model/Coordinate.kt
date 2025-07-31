package com.rizqi.wideloc.domain.model

data class Coordinate(
    val x: Double = 0.0,
    val xOffset: Double = 0.0,
    val y: Double = 0.0,
    val yOffset: Double = 0.0,
) {
    fun areContentsTheSame(other: Coordinate): Boolean {
        return this.x == other.x &&
                this.xOffset == other.xOffset &&
                this.y == other.y &&
                this.yOffset == other.yOffset
    }
}