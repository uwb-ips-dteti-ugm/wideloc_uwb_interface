package com.rizqi.wideloc.di

import com.rizqi.wideloc.data.local.DeviceDataSource
import com.rizqi.wideloc.data.local.DeviceDataSourceImpl
import com.rizqi.wideloc.data.websocket.WideLocSocketDataSource
import com.rizqi.wideloc.data.websocket.WideLocSocketDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindDeviceDataSource(deviceDataSourceImpl: DeviceDataSourceImpl): DeviceDataSource

    @Binds
    abstract fun bindSocketDataSource(wideLocSocketDataSourceImpl: WideLocSocketDataSourceImpl): WideLocSocketDataSource

}