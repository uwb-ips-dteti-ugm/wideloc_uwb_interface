package com.rizqi.wideloc.data.websocket.response

import com.google.gson.annotations.SerializedName

data class TrackingResponse(
    @field:SerializedName("x")
    val x: Double,

    @field:SerializedName("y")
    val y: Double,

    @field:SerializedName("z")
    val z: Double,
)