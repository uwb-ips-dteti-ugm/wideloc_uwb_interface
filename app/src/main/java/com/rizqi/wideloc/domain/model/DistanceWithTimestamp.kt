package com.rizqi.wideloc.domain.model

data class DistancesWithTimestamp(
    val timestamp: Long,
    val distances: List<Distance>
)
