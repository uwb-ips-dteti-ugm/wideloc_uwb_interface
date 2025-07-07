package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.dao.MapDao
import com.rizqi.wideloc.data.local.entity.MapEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MapDataSourceImpl @Inject constructor(
    private val mapDao: MapDao
) : MapDataSource {

    override fun getAllMaps(): Flow<List<MapEntity>> = mapDao.getAll()

    override suspend fun getMapById(id: String): MapEntity? = mapDao.getById(id)

    override suspend fun insertMap(map: MapEntity) {
        mapDao.insert(map)
    }

    override suspend fun insertMaps(maps: List<MapEntity>) {
        mapDao.insertAll(maps)
    }

    override suspend fun updateMap(map: MapEntity) {
        mapDao.update(map)
    }

    override suspend fun deleteMap(map: MapEntity) {
        mapDao.delete(map)
    }

    override suspend fun deleteMapById(id: String) {
        mapDao.deleteById(id)
    }

    override suspend fun deleteAllMaps() {
        mapDao.deleteAll()
    }

}
