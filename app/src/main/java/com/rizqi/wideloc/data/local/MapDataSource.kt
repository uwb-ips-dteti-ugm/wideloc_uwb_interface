package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.entity.MapEntity
import kotlinx.coroutines.flow.Flow

interface MapDataSource {

    fun getAllMaps(): Flow<List<MapEntity>>

    suspend fun getMapById(id: String): MapEntity?

    suspend fun insertMap(map: MapEntity)

    suspend fun insertMaps(maps: List<MapEntity>)

    suspend fun updateMap(map: MapEntity)

    suspend fun deleteMap(map: MapEntity)

    suspend fun deleteMapById(id: String)

    suspend fun deleteAllMaps()
}
