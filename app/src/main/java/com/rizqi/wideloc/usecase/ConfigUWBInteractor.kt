package com.rizqi.wideloc.usecase

import android.util.Log
import com.rizqi.wideloc.domain.model.UWBConfigData
import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.repository.UWBDeviceRepository
import javax.inject.Inject

class ConfigUWBInteractor @Inject constructor(
    private val uwbDeviceRepository: UWBDeviceRepository
) : ConfigUWBUseCase{
    override suspend fun invoke(dns: String, uwbConfigData: UWBConfigData): Boolean {
        return uwbDeviceRepository.configUWB("${dns}.local/", uwbConfigData)
    }
}
