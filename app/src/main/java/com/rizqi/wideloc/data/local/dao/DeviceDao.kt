package com.rizqi.wideloc.data.local.dao

import androidx.room.*
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.DeviceRole
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: DeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(devices: List<DeviceEntity>)

    @Update
    suspend fun update(device: DeviceEntity)

    @Delete
    suspend fun delete(device: DeviceEntity)

    @Query("DELETE FROM devices WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM devices WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): DeviceEntity?

    @Query("SELECT * FROM devices")
    fun getAll(): Flow<List<DeviceEntity>>

    @Query("DELETE FROM devices")
    suspend fun deleteAll()

    @Query("SELECT * FROM devices WHERE role = :role")
    suspend fun getByRole(role: DeviceRole): List<DeviceEntity>

    @Query("SELECT * FROM devices WHERE role = :role LIMIT 1")
    suspend fun getFirstByRole(role: DeviceRole): DeviceEntity?
}
