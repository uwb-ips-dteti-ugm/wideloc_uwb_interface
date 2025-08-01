package com.rizqi.wideloc.domain.model

data class StatisticData(
    val id: String,
    val name: String,
    val unit: String,
    val data: List<StatisticDatum>
)
