package com.rizqi.wideloc.data.network.dto

import com.google.gson.annotations.SerializedName

data class ClientInfoDto(
    val clients: List<ClientDto>
)

data class ClientDto(
    val address: Int,
    val mode: Int,
    @SerializedName("last_update") val lastUpdate: Int
)


