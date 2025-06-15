package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.WifiConnectData
import com.rizqi.wideloc.domain.repository.UWBDeviceRepository
import javax.inject.Inject

class ConnectWifiInteractor @Inject constructor(
    private val uwbDeviceRepository: UWBDeviceRepository
) : ConnectWifiUseCase{
    override suspend fun invoke(wifiConnectData: WifiConnectData): Boolean {
        return uwbDeviceRepository.connectWifi(wifiConnectData)
    }
}
