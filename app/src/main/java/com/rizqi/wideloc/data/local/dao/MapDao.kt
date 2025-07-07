package com.rizqi.wideloc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rizqi.wideloc.data.local.entity.MapEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MapDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(map: MapEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(maps: List<MapEntity>)

    @Update
    suspend fun update(map: MapEntity)

    @Delete
    suspend fun delete(map: MapEntity)

    @Query("DELETE FROM maps WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM maps WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): MapEntity?

    @Query("SELECT * FROM maps")
    fun getAll(): Flow<List<MapEntity>>

    @Query("DELETE FROM maps")
    suspend fun deleteAll()
}