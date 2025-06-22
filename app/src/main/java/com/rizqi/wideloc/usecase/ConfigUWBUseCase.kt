package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.domain.model.UWBConfigData

interface ConfigUWBUseCase {
    suspend fun invoke(dns: String, uwbConfigData: UWBConfigData): Boolean
}
