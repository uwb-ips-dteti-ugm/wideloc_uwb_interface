package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.DeviceRole
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

    suspend fun getByRole(role: DeviceRole): List<DeviceEntity>

    suspend fun getFirstByRole(role: DeviceRole): DeviceEntity?

    suspend fun getNetworkAddressLastId(): Int?

    suspend fun getDeviceAddressLastId(): Int?

    suspend fun getDeviceByDeviceAddress(deviceAddress: Int): DeviceEntity?

    suspend fun getAnchorsByNetworkAddress(networkAddress: Int): List<DeviceEntity>

    suspend fun getClientsByNetworkAddress(networkAddress: Int): List<DeviceEntity>
}
