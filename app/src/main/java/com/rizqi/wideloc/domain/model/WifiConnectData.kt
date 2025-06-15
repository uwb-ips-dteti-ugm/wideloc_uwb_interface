package com.rizqi.wideloc.domain.model

data class WifiConnectData(
    val autoConnect: Boolean,
    val apSSID: String,
    val apPassword: String,
    val staSSID: String,
    val staPassword: String,
)
