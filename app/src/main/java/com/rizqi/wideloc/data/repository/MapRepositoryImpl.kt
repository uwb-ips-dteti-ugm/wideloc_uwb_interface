package com.rizqi.wideloc.data.repository

import com.rizqi.wideloc.data.local.MapDataSource
import com.rizqi.wideloc.domain.model.MapData
import com.rizqi.wideloc.domain.repository.MapRepository
import com.rizqi.wideloc.utils.DomainDataMapper.toMapData
import com.rizqi.wideloc.utils.DomainDataMapper.toMapEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val localDataSource: MapDataSource
) : MapRepository {

    override fun getAllMaps(): Flow<List<MapData>> =
        localDataSource.getAllMaps().map { entityList ->
            entityList.map { entity ->
                entity.toMapData()
            }
        }

    override suspend fun getMapById(id: String): MapData? =
        localDataSource.getMapById(id)?.toMapData()

    override suspend fun insertMap(map: MapData) =
        localDataSource.insertMap(map.toMapEntity())

    override suspend fun insertMaps(maps: List<MapData>) =
        localDataSource.insertMaps(maps.map {
            it.toMapEntity()
        })

    override suspend fun updateMap(map: MapData) =
        localDataSource.updateMap(map.toMapEntity())

    override suspend fun deleteMap(map: MapData) =
        localDataSource.deleteMap(map.toMapEntity())

    override suspend fun deleteMapById(id: String) =
        localDataSource.deleteMapById(id)

    override suspend fun deleteAllMaps() =
        localDataSource.deleteAllMaps()

}
