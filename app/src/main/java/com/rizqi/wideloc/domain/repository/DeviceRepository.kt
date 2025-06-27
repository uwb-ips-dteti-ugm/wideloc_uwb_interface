package com.rizqi.wideloc.domain.repository

import com.rizqi.wideloc.data.local.entity.DeviceRole
import com.rizqi.wideloc.domain.model.DeviceData
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {

    fun getAllDevices(): Flow<List<DeviceData>>

    suspend fun getDeviceById(id: String): DeviceData?

    suspend fun insertDevice(device: DeviceData)

    suspend fun insertDevices(devices: List<DeviceData>)

    suspend fun updateDevice(device: DeviceData)

    suspend fun deleteDevice(device: DeviceData)

    suspend fun deleteDeviceById(id: String)

    suspend fun deleteAllDevices()

    suspend fun getByRole(role: DeviceRole): List<DeviceData>

    suspend fun getFirstByRole(role: DeviceRole): DeviceData?

    suspend fun getNetworkAddressLastId(): Int?

    suspend fun getDeviceAddressLastId(): Int?

    suspend fun getDeviceByDeviceAddress(deviceAddress: Int): DeviceData?
}
