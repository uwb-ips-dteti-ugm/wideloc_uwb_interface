package com.rizqi.wideloc.di

import android.content.Context
import com.rizqi.wideloc.data.local.WideLocDatabase
import com.rizqi.wideloc.data.local.dao.DeviceDao
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
}