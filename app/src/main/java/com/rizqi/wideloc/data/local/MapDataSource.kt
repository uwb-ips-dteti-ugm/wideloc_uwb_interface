package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.entity.MapEntity
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for accessing and manipulating stored [MapEntity] records.
 *
 * This interface defines all operations related to managing indoor map data,
 * including CRUD actions and reactive retrieval via [Flow]. Implementations of
 * this data source typically interact with a local Room database.
 *
 * Indoor maps can represent layout images, environment metadata, or other
 * spatial resources used throughout the WideLoc system.
 */
interface MapDataSource {

    /**
     * Returns a reactive stream of all stored maps.
     *
     * @return A [Flow] that emits the list of [MapEntity] whenever changes occur.
     */
    fun getAllMaps(): Flow<List<MapEntity>>

    /**
     * Retrieves a map by its unique identifier.
     *
     * @param id The unique ID of the map.
     * @return The matching [MapEntity], or `null` if not found.
     */
    suspend fun getMapById(id: String): MapEntity?

    /**
     * Inserts a single map into the data source.
     *
     * @param map The [MapEntity] to store.
     */
    suspend fun insertMap(map: MapEntity)

    /**
     * Inserts multiple maps into the data source.
     *
     * @param maps A list of [MapEntity] instances to store.
     */
    suspend fun insertMaps(maps: List<MapEntity>)

    /**
     * Updates an existing map.
     *
     * @param map The [MapEntity] containing updated fields.
     */
    suspend fun updateMap(map: MapEntity)

    /**
     * Removes a map from the data source.
     *
     * @param map The map to delete.
     */
    suspend fun deleteMap(map: MapEntity)

    /**
     * Deletes a map based on its unique ID.
     *
     * @param id The ID of the map to delete.
     */
    suspend fun deleteMapById(id: String)

    /**
     * Deletes all maps from the data source.
     * This operation is destructive and should be used cautiously.
     */
    suspend fun deleteAllMaps()
}
