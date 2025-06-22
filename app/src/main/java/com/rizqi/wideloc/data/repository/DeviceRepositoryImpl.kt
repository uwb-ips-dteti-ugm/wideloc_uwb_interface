package com.rizqi.wideloc.data.repository

import android.util.Log
import com.rizqi.wideloc.data.local.DeviceDataSource
import com.rizqi.wideloc.data.local.entity.DeviceRole
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.repository.DeviceRepository
import com.rizqi.wideloc.utils.DomainDataMapper.asDeviceData
import com.rizqi.wideloc.utils.DomainDataMapper.asDeviceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val localDataSource: DeviceDataSource
) : DeviceRepository {

    override fun getAllDevices(): Flow<List<DeviceData>> =
        localDataSource.getAllDevices().map { entityList ->
            entityList.map { entity ->
                entity.asDeviceData()
            }
        }

    override suspend fun getDeviceById(id: String): DeviceData? =
        localDataSource.getDeviceById(id)?.asDeviceData()

    override suspend fun insertDevice(device: DeviceData) =
        localDataSource.insertDevice(device.asDeviceEntity())

    override suspend fun insertDevices(devices: List<DeviceData>) =
        localDataSource.insertDevices(devices.map {
            it.asDeviceEntity()
        })

    override suspend fun updateDevice(device: DeviceData) =
        localDataSource.updateDevice(device.asDeviceEntity())

    override suspend fun deleteDevice(device: DeviceData) =
        localDataSource.deleteDevice(device.asDeviceEntity())

    override suspend fun deleteDeviceById(id: String) =
        localDataSource.deleteDeviceById(id)

    override suspend fun deleteAllDevices() =
        localDataSource.deleteAllDevices()

    override suspend fun getByRole(role: DeviceRole): List<DeviceData>  = localDataSource.getByRole(role).map { it.asDeviceData() }

    override suspend fun getFirstByRole(role: DeviceRole): DeviceData? = localDataSource.getFirstByRole(role)?.asDeviceData()

    override suspend fun getNetworkAddressLastId() = localDataSource.getNetworkAddressLastId()

    override suspend fun getDeviceAddressLastId() = localDataSource.getDeviceAddressLastId()

}
