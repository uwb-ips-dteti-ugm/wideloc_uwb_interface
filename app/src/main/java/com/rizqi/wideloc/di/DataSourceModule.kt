package com.rizqi.wideloc.di

import com.rizqi.wideloc.data.local.DeviceDataSource
import com.rizqi.wideloc.data.local.DeviceDataSourceImpl
import com.rizqi.wideloc.data.local.MapDataSource
import com.rizqi.wideloc.data.local.MapDataSourceImpl
import com.rizqi.wideloc.data.local.TWRDataSource
import com.rizqi.wideloc.data.local.TWRDataSourceImpl
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

    @Binds
    abstract fun bindTWRDataSource(twrDataSourceImpl: TWRDataSourceImpl): TWRDataSource

    @Binds
    abstract fun bindMapDataSource(mapDataSourceImpl: MapDataSourceImpl): MapDataSource

}