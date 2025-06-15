package com.rizqi.wideloc.data.network.dto

import com.google.gson.annotations.SerializedName

data class TWRDto(
    @SerializedName("twr_data") val twrData: List<TWRDataDto>
)

data class TWRDataDto(
    val timestamp: Int,
    @SerializedName("addr_1") val addr1: Int,
    @SerializedName("addr_2") val addr2: Int,
    val distance: Double
)
