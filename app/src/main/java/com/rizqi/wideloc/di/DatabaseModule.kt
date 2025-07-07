package com.rizqi.wideloc.di

import android.content.Context
import com.rizqi.wideloc.data.local.WideLocDatabase
import com.rizqi.wideloc.data.local.dao.DeviceDao
import com.rizqi.wideloc.data.local.dao.MapDao
import com.rizqi.wideloc.data.local.dao.TWRDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideWideLocDatabase(
        @ApplicationContext context: Context
    ) : WideLocDatabase = WideLocDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideDeviceDao(database: WideLocDatabase): DeviceDao = database.deviceDao()

    @Provides
    @Singleton
    fun provideTWRDataDao(database: WideLocDatabase): TWRDataDao = database.twrDataDao()

    @Provides
    @Singleton
    fun provideMapDao(database: WideLocDatabase): MapDao = database.mapDao()
}