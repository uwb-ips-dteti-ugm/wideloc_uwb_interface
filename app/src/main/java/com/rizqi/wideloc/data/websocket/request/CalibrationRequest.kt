package com.rizqi.wideloc.data.websocket.request

import com.google.gson.annotations.SerializedName

data class CalibrationRequest(
    @field:SerializedName("data")
    val data: String
)
