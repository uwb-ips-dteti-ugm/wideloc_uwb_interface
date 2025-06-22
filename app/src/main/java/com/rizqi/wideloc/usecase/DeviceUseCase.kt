package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.data.local.entity.DeviceRole
import com.rizqi.wideloc.domain.model.DeviceData
import kotlinx.coroutines.flow.Flow

interface DeviceUseCase {

    fun getAllDevices(): Flow<List<DeviceData>>

    fun getAvailableDevices(): Flow<List<DeviceData>>

    fun getReconfigureDevices(): Flow<List<DeviceData>>

    suspend fun getDeviceById(id: String): DeviceData?

    suspend fun insertDevice(device: DeviceData)

    suspend fun insertDevices(devices: List<DeviceData>)

    suspend fun updateDevice(device: DeviceData)

    suspend fun deleteDevice(device: DeviceData)

    suspend fun deleteDeviceById(id: String)

    suspend fun deleteAllDevices()

    fun validateSocketUrl(url: String): Result<Boolean>

    suspend fun getByRole(role: DeviceRole): List<DeviceData>

    suspend fun getFirstByRole(role: DeviceRole): DeviceData?

    suspend fun isAnyServerSaved(): Boolean

    suspend fun generateNetworkAddress(): Int

    suspend fun generateDeviceAddress(): Int
}
