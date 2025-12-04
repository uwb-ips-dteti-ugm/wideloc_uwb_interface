package com.rizqi.wideloc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rizqi.wideloc.data.local.entity.MapEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing [MapEntity] records stored in the `maps` table.
 *
 * A [MapEntity] typically represents an indoor map used by the WideLoc system,
 * including room layouts, anchor positions, map scale, or other metadata
 * relevant to UWB-based indoor positioning.
 *
 * This DAO provides CRUD operations along with basic queries to retrieve
 * all maps or a map by its unique identifier.
 */
@Dao
interface MapDao {

    /**
     * Inserts a single map into the database.
     *
     * If a map with the same ID already exists, it will be replaced.
     *
     * @param map the map entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(map: MapEntity)

    /**
     * Inserts multiple maps into the database.
     *
     * Existing records with matching IDs will be replaced.
     *
     * @param maps the list of map entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(maps: List<MapEntity>)

    /**
     * Updates an existing map record in the database.
     *
     * @param map the updated map entity.
     */
    @Update
    suspend fun update(map: MapEntity)

    /**
     * Deletes a specific map entry from the database.
     *
     * @param map the map entity to delete.
     */
    @Delete
    suspend fun delete(map: MapEntity)

    /**
     * Deletes a map based on its unique ID.
     *
     * @param id the unique identifier of the map.
     */
    @Query("DELETE FROM maps WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Retrieves a map by its ID.
     *
     * @param id the unique identifier of the map.
     * @return the matching [MapEntity], or `null` if not found.
     */
    @Query("SELECT * FROM maps WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): MapEntity?

    /**
     * Retrieves all maps as a reactive [Flow].
     *
     * A Flow allows the UI to automatically receive updates
     * when map data changes in the database.
     *
     * @return a flow emitting the list of all saved maps.
     */
    @Query("SELECT * FROM maps")
    fun getAll(): Flow<List<MapEntity>>

    /**
     * Deletes all map entries from the database.
     *
     * Use with caution, as this action cannot be undone.
     */
    @Query("DELETE FROM maps")
    suspend fun deleteAll()
}
