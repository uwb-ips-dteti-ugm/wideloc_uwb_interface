package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.Point

interface KalmanFilterUseCase {
    fun update(
       measurement: Point
    ): Point
}