package com.rizqi.wideloc.di

import com.rizqi.wideloc.data.repository.DeviceRepositoryImpl
import com.rizqi.wideloc.data.repository.TrackingRepositoryImpl
import com.rizqi.wideloc.domain.DeviceRepository
import com.rizqi.wideloc.domain.repository.TrackingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTrackingRepository(repositoryImpl: TrackingRepositoryImpl): TrackingRepository


    @Binds
    abstract fun bindDeviceRepository(repositoryImpl: DeviceRepositoryImpl): DeviceRepository
}