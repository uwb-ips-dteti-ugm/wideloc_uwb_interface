package com.rizqi.wideloc.domain.model

import com.rizqi.wideloc.data.local.entity.UWBMode

data class UWBConfigData(
    val autoStart: Boolean,
    val isServer: Boolean,
    val maxClient: Int,
    val mode: UWBMode,
    val networkAddress: Int,
    val deviceAddress: Int,
)
