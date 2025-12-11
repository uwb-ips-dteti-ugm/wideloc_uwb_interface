package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.DeviceRole
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for accessing and managing device-related data in the local data layer.
 *
 * This interface allows repository or data modules to interact with device records
 * without depending directly on Room implementations. It defines methods for creating,
 * reading, updating, deleting, and querying devices based on roles, UWB addresses,
 * and network assignments.
 *
 * Implementations of this interface are typically backed by Room DAOs.
 */
interface DeviceDataSource {

    /**
     * Returns a flow of all stored devices.
     * UI can collect this to receive automatic updates when data changes.
     */
    fun getAllDevices(): Flow<List<DeviceEntity>>

    /**
     * Retrieves a single device by its unique ID.
     *
     * @param id The device identifier.
     * @return The matching device or null if not found.
     */
    suspend fun getDeviceById(id: String): DeviceEntity?

    /**
     * Inserts a new device or updates it if it already exists.
     */
    suspend fun insertDevice(device: DeviceEntity)

    /**
     * Inserts or replaces multiple devices in bulk.
     */
    suspend fun insertDevices(devices: List<DeviceEntity>)

    /**
     * Updates the data of an existing device.
     */
    suspend fun updateDevice(device: DeviceEntity)

    /**
     * Deletes a device entry.
     */
    suspend fun deleteDevice(device: DeviceEntity)

    /**
     * Deletes a device using its ID.
     *
     * @param id Device ID.
     */
    suspend fun deleteDeviceById(id: String)

    /**
     * Deletes all device entries.
     */
    suspend fun deleteAllDevices()

    /**
     * Retrieves all devices that match a specific role (Server, Anchor, Client).
     */
    suspend fun getByRole(role: DeviceRole): List<DeviceEntity>

    /**
     * Retrieves the first device that matches a role.
     *
     * @return A device or null if none match.
     */
    suspend fun getFirstByRole(role: DeviceRole): DeviceEntity?

    /**
     * Gets the most recently stored UWB network address.
     *
     * @return Latest network address or null if none exist.
     */
    suspend fun getNetworkAddressLastId(): Int?

    /**
     * Gets the most recently stored UWB device address.
     *
     * @return Latest device address or null if none exist.
     */
    suspend fun getDeviceAddressLastId(): Int?

    /**
     * Retrieves a device by its UWB device address.
     */
    suspend fun getDeviceByDeviceAddress(deviceAddress: Int): DeviceEntity?

    /**
     * Retrieves all Anchor devices associated with a given network address.
     */
    suspend fun getAnchorsByNetworkAddress(networkAddress: Int): List<DeviceEntity>

    /**
     * Retrieves all Client devices associated with a given network address.
     */
    suspend fun getClientsByNetworkAddress(networkAddress: Int): List<DeviceEntity>
}
