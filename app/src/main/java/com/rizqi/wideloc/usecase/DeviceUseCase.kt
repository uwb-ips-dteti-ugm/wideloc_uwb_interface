package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

interface DeviceUseCase {

    fun getAllDevices(): Flow<List<DeviceEntity>>

    suspend fun getDeviceById(id: String): DeviceEntity?

    suspend fun insertDevice(device: DeviceEntity)

    suspend fun insertDevices(devices: List<DeviceEntity>)

    suspend fun updateDevice(device: DeviceEntity)

    suspend fun deleteDevice(device: DeviceEntity)

    suspend fun deleteDeviceById(id: String)

    suspend fun deleteAllDevices()

    fun validateSocketUrl(url: String): Result<Boolean>
}
