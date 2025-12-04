package com.rizqi.wideloc.data.local.dao

import androidx.room.*
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.DeviceRole
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) untuk mengelola entitas [DeviceEntity] dalam tabel "devices".
 *
 * DAO ini menyediakan operasi CRUD (Create, Read, Update, Delete) serta
 * query tambahan terkait konfigurasi UWB seperti pencarian alamat device dan network address terakhir.
 *
 * Tabel `devices` menyimpan perangkat-perangkat UWB dalam aplikasi WideLoc,
 * termasuk Anchor, Tag, dan role lainnya.
 */
@Dao
interface DeviceDao {

    /**
     * Menyimpan satu device ke database.
     * Jika ID sudah ada, akan ditimpa (REPLACE).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: DeviceEntity)

    /**
     * Menyimpan banyak device sekaligus.
     * Jika ID sudah ada, akan ditimpa.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(devices: List<DeviceEntity>)

    /**
     * Memperbarui data suatu device.
     */
    @Update
    suspend fun update(device: DeviceEntity)

    /**
     * Menghapus data satu device.
     */
    @Delete
    suspend fun delete(device: DeviceEntity)

    /**
     * Menghapus device berdasarkan ID.
     *
     * @param id ID unik perangkat.
     */
    @Query("DELETE FROM devices WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Mengambil satu device berdasarkan ID.
     *
     * @return [DeviceEntity] atau null jika tidak ditemukan.
     */
    @Query("SELECT * FROM devices WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): DeviceEntity?

    /**
     * Mengambil semua device sebagai Flow.
     * Flow membuat UI otomatis update ketika ada perubahan data.
     */
    @Query("SELECT * FROM devices")
    fun getAll(): Flow<List<DeviceEntity>>

    /**
     * Menghapus seluruh data device.
     * Hati-hati saat menggunakannya.
     */
    @Query("DELETE FROM devices")
    suspend fun deleteAll()

    /**
     * Mengambil semua device dengan role tertentu (Anchor/Tag).
     *
     * @param role Role perangkat (enum: DeviceRole).
     */
    @Query("SELECT * FROM devices WHERE role = :role")
    suspend fun getByRole(role: DeviceRole): List<DeviceEntity>

    /**
     * Mengambil device pertama berdasarkan role tertentu.
     *
     * @return Device pertama yang cocok atau null jika tidak ada.
     */
    @Query("SELECT * FROM devices WHERE role = :role LIMIT 1")
    suspend fun getFirstByRole(role: DeviceRole): DeviceEntity?

    /**
     * Mengambil UWB device address terakhir yang pernah disimpan.
     *
     * Digunakan untuk setup perangkat baru agar increment address.
     *
     * @return Alamat device terakhir atau null jika belum ada.
     */
    @Query("SELECT uwb_config_device_address FROM devices WHERE uwb_config_device_address IS NOT NULL ORDER BY created_at DESC LIMIT 1")
    suspend fun getLastUwbDeviceAddress(): Int?

    /**
     * Mengambil network address terakhir yang pernah disimpan.
     *
     * @return Network address terakhir atau null jika belum ada.
     */
    @Query("SELECT uwb_config_network_address FROM devices WHERE uwb_config_network_address IS NOT NULL ORDER BY created_at DESC LIMIT 1")
    suspend fun getLastUwbNetworkAddress(): Int?

    /**
     * Mencari device berdasarkan UWB device address spesifik.
     *
     * @param deviceAddress Alamat perangkat UWB (DW3000).
     */
    @Query("SELECT * FROM devices WHERE uwb_config_device_address = :deviceAddress LIMIT 1")
    suspend fun getDeviceByDeviceAddress(deviceAddress: Int): DeviceEntity?

    /**
     * Mengambil semua device berdasarkan role dan network address tertentu.
     *
     * @param role Role perangkat (Anchor/Tag).
     * @param networkAddress Network address UWB.
     */
    @Query("SELECT * FROM devices WHERE role = :role AND uwb_config_network_address = :networkAddress")
    suspend fun getDevicesByRoleAndNetworkAddress(role: DeviceRole, networkAddress: Int): List<DeviceEntity>
}
