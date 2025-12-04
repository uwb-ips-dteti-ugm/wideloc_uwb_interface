package com.rizqi.wideloc.data.local.dao

import androidx.room.*
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.DeviceRole
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing [DeviceEntity] records in the `devices` table.
 *
 * This DAO provides full CRUD operations along with additional queries
 * used for UWB device configuration, such as retrieving the most recent
 * device address or network address.
 *
 * The `devices` table stores all UWB devices registered in the WideLoc application,
 * including Anchors, Tags, and other device roles defined by [DeviceRole].
 */
@Dao
interface DeviceDao {

    /**
     * Inserts a single device into the database.
     *
     * If a device with the same ID already exists, it will be replaced.
     *
     * @param device the device entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: DeviceEntity)

    /**
     * Inserts multiple devices into the database.
     *
     * Existing devices with matching IDs will be replaced.
     *
     * @param devices the list of device entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(devices: List<DeviceEntity>)

    /**
     * Updates a device record in the database.
     *
     * @param device the updated device entity.
     */
    @Update
    suspend fun update(device: DeviceEntity)

    /**
     * Deletes a specific device from the database.
     *
     * @param device the device entity to delete.
     */
    @Delete
    suspend fun delete(device: DeviceEntity)

    /**
     * Deletes a device based on its unique ID.
     *
     * @param id the unique identifier of the device.
     */
    @Query("DELETE FROM devices WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Retrieves a device by its ID.
     *
     * @param id the unique identifier of the device.
     * @return the matching [DeviceEntity], or `null` if no device is found.
     */
    @Query("SELECT * FROM devices WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): DeviceEntity?

    /**
     * Retrieves all stored devices as a reactive [Flow].
     *
     * A Flow allows the UI to automatically receive updates when device data changes.
     *
     * @return a flow emitting the list of all devices.
     */
    @Query("SELECT * FROM devices")
    fun getAll(): Flow<List<DeviceEntity>>

    /**
     * Deletes all device records from the database.
     *
     * Use with caution as this action is irreversible.
     */
    @Query("DELETE FROM devices")
    suspend fun deleteAll()

    /**
     * Retrieves all devices with a specific role (e.g., Anchor, Tag).
     *
     * @param role the device role filter.
     * @return a list of devices matching the role.
     */
    @Query("SELECT * FROM devices WHERE role = :role")
    suspend fun getByRole(role: DeviceRole): List<DeviceEntity>

    /**
     * Retrieves the first device matching a specific role.
     *
     * @param role the target device role.
     * @return the first matching device, or `null` if none exist.
     */
    @Query("SELECT * FROM devices WHERE role = :role LIMIT 1")
    suspend fun getFirstByRole(role: DeviceRole): DeviceEntity?

    /**
     * Retrieves the most recently created non-null UWB device address.
     *
     * Useful for generating the next incremental UWB address during device setup.
     *
     * @return the last UWB device address, or `null` if not available.
     */
    @Query(
        """
        SELECT uwb_config_device_address 
        FROM devices 
        WHERE uwb_config_device_address IS NOT NULL 
        ORDER BY created_at DESC 
        LIMIT 1
        """
    )
    suspend fun getLastUwbDeviceAddress(): Int?

    /**
     * Retrieves the most recently created non-null UWB network address.
     *
     * @return the last UWB network address, or `null` if not available.
     */
    @Query(
        """
        SELECT uwb_config_network_address 
        FROM devices 
        WHERE uwb_config_network_address IS NOT NULL 
        ORDER BY created_at DESC 
        LIMIT 1
        """
    )
    suspend fun getLastUwbNetworkAddress(): Int?

    /**
     * Retrieves a device based on its UWB device address.
     *
     * @param deviceAddress the UWB device address to look for.
     * @return the matching device, or `null` if not found.
     */
    @Query("SELECT * FROM devices WHERE uwb_config_device_address = :deviceAddress LIMIT 1")
    suspend fun getDeviceByDeviceAddress(deviceAddress: Int): DeviceEntity?

    /**
     * Retrieves all devices that match both the role and network address.
     *
     * @param role the role of the device (e.g., Anchor, Tag).
     * @param networkAddress the UWB network address.
     * @return a list of devices matching both conditions.
     */
    @Query("SELECT * FROM devices WHERE role = :role AND uwb_config_network_address = :networkAddress")
    suspend fun getDevicesByRoleAndNetworkAddress(role: DeviceRole, networkAddress: Int): List<DeviceEntity>
}
