package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.WifiConfigData

interface ConfigWifiUseCase {
    suspend fun invoke(wifiConfigData: WifiConfigData): Boolean
}
