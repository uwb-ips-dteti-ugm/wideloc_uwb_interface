package com.rizqi.wideloc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.rizqi.wideloc.data.local.entity.TWRDataEntity

/**
 * Data Access Object (DAO) for managing [TWRDataEntity] objects,
 * which represent Two-Way Ranging (TWR) measurement data collected
 * by the Ultra-Wideband (UWB) tracking system in the WideLoc project.
 *
 * TWR data typically includes fields such as timestamp, computed distance,
 * anchor/tag device identifiers, and additional metadata used for processing
 * and position estimation algorithms (e.g., trilateration, Kalman filtering).
 *
 * This DAO provides basic insertion operations for saving individual TWR records
 * or bulk datasets. The insert operations use [OnConflictStrategy.REPLACE],
 * ensuring that existing entries with the same primary key are overwritten.
 */
@Dao
interface TWRDataDao {

    /**
     * Inserts a single TWR data record into the database.
     *
     * If a conflict occurs (e.g., the same primary key already exists),
     * the existing record will be replaced according to the
     * [OnConflictStrategy.REPLACE] policy.
     *
     * @param twrDataEntity The TWR data entity to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(twrDataEntity: TWRDataEntity)

    /**
     * Inserts a list of TWR data records in a single batch.
     *
     * This is useful for efficiently storing large amounts of TWR data,
     * which is commonly produced during a tracking session. Conflicting
     * entries will be replaced.
     *
     * @param twrDataEntities The list of TWR data entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(twrDataEntities: List<TWRDataEntity>)
}
