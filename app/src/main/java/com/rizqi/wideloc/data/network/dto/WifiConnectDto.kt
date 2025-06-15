package com.rizqi.wideloc.data.network.dto

import com.google.gson.annotations.SerializedName

data class WifiConnectDto(
    @SerializedName("autoconnect") val autoConnect: Boolean,
    @SerializedName("ap_ssid") val apSSID: String,
    @SerializedName("ap_pass") val apPassword: String,
    @SerializedName("sta_ssid") val staSSID: String,
    @SerializedName("sta_pass") val staPassword: String,
)
