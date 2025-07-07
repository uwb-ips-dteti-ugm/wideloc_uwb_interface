package com.rizqi.wideloc.di

import com.rizqi.wideloc.data.repository.DeviceRepositoryImpl
import com.rizqi.wideloc.data.repository.MapRepositoryImpl
import com.rizqi.wideloc.data.repository.TrackingRepositoryImpl
import com.rizqi.wideloc.data.repository.UWBDeviceRepositoryImpl
import com.rizqi.wideloc.domain.repository.DeviceRepository
import com.rizqi.wideloc.domain.repository.MapRepository
import com.rizqi.wideloc.domain.repository.TrackingRepository
import com.rizqi.wideloc.domain.repository.UWBDeviceRepository
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

    @Binds
    abstract fun bindUWBDeviceRepository(repositoryImpl: UWBDeviceRepositoryImpl): UWBDeviceRepository

    @Binds
    abstract fun bindMapRepository(repositoryImpl: MapRepositoryImpl): MapRepository
}