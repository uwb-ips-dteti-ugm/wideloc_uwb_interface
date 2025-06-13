package com.rizqi.wideloc.di

import com.rizqi.wideloc.usecase.DeviceInteractor
import com.rizqi.wideloc.usecase.DeviceUseCase
import com.rizqi.wideloc.usecase.GenerateIDInteractor
import com.rizqi.wideloc.usecase.GenerateIDUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindDeviceUseCase(useCase: DeviceInteractor): DeviceUseCase
}