package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.repository.UWBDeviceRepository
import javax.inject.Inject

class ConfigWifiInteractor @Inject constructor(
    private val uwbDeviceRepository: UWBDeviceRepository
) : ConfigWifiUseCase{
    override suspend fun invoke(wifiConfigData: WifiConfigData): Boolean {
        return uwbDeviceRepository.configWifi(wifiConfigData)
    }
}
