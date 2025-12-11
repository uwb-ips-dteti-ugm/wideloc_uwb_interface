package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.dao.DeviceDao
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.DeviceRole
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [DeviceDataSource] backed by a Room-based [DeviceDao].
 *
 * This class acts as the local data provider for device-related operations,
 * delegating all database interactions to the underlying DAO. It is typically
 * used inside repositories or use cases, providing an abstraction layer
 * between the domain layer and the database.
 *
 * @property deviceDao The Room DAO responsible for performing CRUD operations on devices.
 */
class DeviceDataSourceImpl @Inject constructor(
    private val deviceDao: DeviceDao
) : DeviceDataSource {

    /** @inheritdoc */
    override fun getAllDevices(): Flow<List<DeviceEntity>> = deviceDao.getAll()

    /** @inheritdoc */
    override suspend fun getDeviceById(id: String): DeviceEntity? = deviceDao.getById(id)

    /** @inheritdoc */
    override suspend fun insertDevice(device: DeviceEntity) {
        deviceDao.insert(device)
    }

    /** @inheritdoc */
    override suspend fun insertDevices(devices: List<DeviceEntity>) {
        deviceDao.insertAll(devices)
    }

    /** @inheritdoc */
    override suspend fun updateDevice(device: DeviceEntity) {
        deviceDao.update(device)
    }

    /** @inheritdoc */
    override suspend fun deleteDevice(device: DeviceEntity) {
        deviceDao.delete(device)
    }

    /** @inheritdoc */
    override suspend fun deleteDeviceById(id: String) {
        deviceDao.deleteById(id)
    }

    /** @inheritdoc */
    override suspend fun deleteAllDevices() {
        deviceDao.deleteAll()
    }

    /** @inheritdoc */
    override suspend fun getByRole(role: DeviceRole): List<DeviceEntity> =
        deviceDao.getByRole(role)

    /** @inheritdoc */
    override suspend fun getFirstByRole(role: DeviceRole): DeviceEntity? =
        deviceDao.getFirstByRole(role)

    /** @inheritdoc */
    override suspend fun getNetworkAddressLastId() =
        deviceDao.getLastUwbNetworkAddress()

    /** @inheritdoc */
    override suspend fun getDeviceAddressLastId() =
        deviceDao.getLastUwbDeviceAddress()

    /** @inheritdoc */
    override suspend fun getDeviceByDeviceAddress(deviceAddress: Int): DeviceEntity? =
        deviceDao.getDeviceByDeviceAddress(deviceAddress)

    /** @inheritdoc */
    override suspend fun getAnchorsByNetworkAddress(networkAddress: Int): List<DeviceEntity> =
        deviceDao.getDevicesByRoleAndNetworkAddress(DeviceRole.Anchor, networkAddress)

    /** @inheritdoc */
    override suspend fun getClientsByNetworkAddress(networkAddress: Int): List<DeviceEntity> =
        deviceDao.getDevicesByRoleAndNetworkAddress(DeviceRole.Client, networkAddress)
}
