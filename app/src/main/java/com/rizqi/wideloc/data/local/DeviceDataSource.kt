package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

interface DeviceDataSource {

    fun getAllDevices(): Flow<List<DeviceEntity>>

    suspend fun getDeviceById(id: String): DeviceEntity?

    suspend fun insertDevice(device: DeviceEntity)

    suspend fun insertDevices(devices: List<DeviceEntity>)

    suspend fun updateDevice(device: DeviceEntity)

    suspend fun deleteDevice(device: DeviceEntity)

    suspend fun deleteDeviceById(id: String)

    suspend fun deleteAllDevices()
}
