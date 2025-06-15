package com.rizqi.wideloc.di

import com.rizqi.wideloc.usecase.ConfigWifiInteractor
import com.rizqi.wideloc.usecase.ConfigWifiUseCase
import com.rizqi.wideloc.usecase.ConnectWifiInteractor
import com.rizqi.wideloc.usecase.ConnectWifiUseCase
import com.rizqi.wideloc.usecase.DeviceInteractor
import com.rizqi.wideloc.usecase.DeviceUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindDeviceUseCase(useCase: DeviceInteractor): DeviceUseCase

    @Binds
    abstract fun bindConfigWifiUseCase(useCase: ConfigWifiInteractor): ConfigWifiUseCase

    @Binds
    abstract fun bindConnectWifiUseCase(useCase: ConnectWifiInteractor): ConnectWifiUseCase
}