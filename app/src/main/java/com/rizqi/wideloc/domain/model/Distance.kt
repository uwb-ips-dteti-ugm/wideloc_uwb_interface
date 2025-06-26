package com.rizqi.wideloc.domain.model

data class Distance(
    val id: String,
    val point1: Point,
    val point2: Point,
    val distance: Double,
    val timestamp: Long,
) {
    fun withPoint1X(newValue: Double): Distance {
        return this.copy(point1 = point1.copy(x = point1.x.copy(value = newValue)))
    }
    fun withPoint2X(newValue: Double): Distance {
        return this.copy(point2 = point2.copy(x = point2.x.copy(value = newValue)))
    }
    fun withPoint1Y(newValue: Double): Distance {
        return this.copy(point1 = point1.copy(y = point1.y.copy(value = newValue)))
    }
    fun withPoint2Y(newValue: Double): Distance {
        return this.copy(point2 = point2.copy(y = point2.y.copy(value = newValue)))
    }
}