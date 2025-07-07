package com.rizqi.wideloc.domain.repository

import com.rizqi.wideloc.domain.model.MapData
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    fun getAllMaps(): Flow<List<MapData>>

    suspend fun getMapById(id: String): MapData?

    suspend fun insertMap(map: MapData)

    suspend fun insertMaps(maps: List<MapData>)

    suspend fun updateMap(map: MapData)

    suspend fun deleteMap(map: MapData)

    suspend fun deleteMapById(id: String)

    suspend fun deleteAllMaps()
}