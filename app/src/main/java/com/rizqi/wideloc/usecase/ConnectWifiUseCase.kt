package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.WifiConnectData

interface ConnectWifiUseCase {
    suspend fun invoke(wifiConnectData: WifiConnectData): Boolean
}
