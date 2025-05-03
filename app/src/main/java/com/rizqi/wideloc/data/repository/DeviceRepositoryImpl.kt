package com.rizqi.wideloc.data.repository

import com.rizqi.wideloc.data.local.DeviceDataSource
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.domain.DeviceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val localDataSource: DeviceDataSource
) : DeviceRepository {

    override fun getAllDevices(): Flow<List<DeviceEntity>> =
        localDataSource.getAllDevices()

    override suspend fun getDeviceById(id: String): DeviceEntity? =
        localDataSource.getDeviceById(id)

    override suspend fun insertDevice(device: DeviceEntity) =
        localDataSource.insertDevice(device)

    override suspend fun insertDevices(devices: List<DeviceEntity>) =
        localDataSource.insertDevices(devices)

    override suspend fun updateDevice(device: DeviceEntity) =
        localDataSource.updateDevice(device)

    override suspend fun deleteDevice(device: DeviceEntity) =
        localDataSource.deleteDevice(device)

    override suspend fun deleteDeviceById(id: String) =
        localDataSource.deleteDeviceById(id)

    override suspend fun deleteAllDevices() =
        localDataSource.deleteAllDevices()
    
}
