package com.rizqi.wideloc.domain.model

data class Point(
    val id: String,
    val x: Variable,
    val y: Variable,
) {
    fun copyWithNewCoordinate(xValue: Double, yValue: Double): Point {
        return this.copy(
            x = x.copy(value = xValue),
            y = y.copy(value = yValue),
        )
    }
}
