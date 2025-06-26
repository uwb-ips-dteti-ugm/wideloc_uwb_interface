package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.dao.DeviceDao
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.DeviceRole
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeviceDataSourceImpl @Inject constructor(
    private val deviceDao: DeviceDao
) : DeviceDataSource {

    override fun getAllDevices(): Flow<List<DeviceEntity>> = deviceDao.getAll()

    override suspend fun getDeviceById(id: String): DeviceEntity? = deviceDao.getById(id)

    override suspend fun insertDevice(device: DeviceEntity) {
        deviceDao.insert(device)
    }

    override suspend fun insertDevices(devices: List<DeviceEntity>) {
        deviceDao.insertAll(devices)
    }

    override suspend fun updateDevice(device: DeviceEntity) {
        deviceDao.update(device)
    }

    override suspend fun deleteDevice(device: DeviceEntity) {
        deviceDao.delete(device)
    }

    override suspend fun deleteDeviceById(id: String) {
        deviceDao.deleteById(id)
    }

    override suspend fun deleteAllDevices() {
        deviceDao.deleteAll()
    }

    override suspend fun getByRole(role: DeviceRole): List<DeviceEntity> = deviceDao.getByRole(role)

    override suspend fun getFirstByRole(role: DeviceRole): DeviceEntity? = deviceDao.getFirstByRole(role)

    override suspend fun getNetworkAddressLastId() = deviceDao.getLastUwbNetworkAddress()

    override suspend fun getDeviceAddressLastId() = deviceDao.getLastUwbDeviceAddress()

    override suspend fun getDeviceByNetworkAddress(networkAddress: Int): DeviceEntity? = deviceDao.getDeviceByNetworkAddress(networkAddress)
}
