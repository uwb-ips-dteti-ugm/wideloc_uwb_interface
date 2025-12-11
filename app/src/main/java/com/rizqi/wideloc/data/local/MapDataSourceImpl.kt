package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.dao.MapDao
import com.rizqi.wideloc.data.local.entity.MapEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [MapDataSource] backed by a Room-based [MapDao].
 *
 * This class serves as the local data provider for managing stored indoor maps.
 * It delegates all CRUD and query operations directly to the underlying DAO,
 * ensuring a clean separation between the data layer and the rest of the app.
 *
 * Typical use cases include providing map data for UI layers,
 * synchronizing map metadata, and managing indoor layout resources
 * used within the WideLoc system.
 *
 * @property mapDao The Room DAO responsible for performing database operations on maps.
 */
class MapDataSourceImpl @Inject constructor(
    private val mapDao: MapDao
) : MapDataSource {

    /** @inheritdoc */
    override fun getAllMaps(): Flow<List<MapEntity>> = mapDao.getAll()

    /** @inheritdoc */
    override suspend fun getMapById(id: String): MapEntity? = mapDao.getById(id)

    /** @inheritdoc */
    override suspend fun insertMap(map: MapEntity) {
        mapDao.insert(map)
    }

    /** @inheritdoc */
    override suspend fun insertMaps(maps: List<MapEntity>) {
        mapDao.insertAll(maps)
    }

    /** @inheritdoc */
    override suspend fun updateMap(map: MapEntity) {
        mapDao.update(map)
    }

    /** @inheritdoc */
    override suspend fun deleteMap(map: MapEntity) {
        mapDao.delete(map)
    }

    /** @inheritdoc */
    override suspend fun deleteMapById(id: String) {
        mapDao.deleteById(id)
    }

    /** @inheritdoc */
    override suspend fun deleteAllMaps() {
        mapDao.deleteAll()
    }
}
