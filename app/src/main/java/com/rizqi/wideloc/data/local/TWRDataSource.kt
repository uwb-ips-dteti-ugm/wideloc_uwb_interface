package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.entity.TWRDataEntity

/**
 * Abstraction for storing Two-Way Ranging (TWR) measurement data.
 *
 * Implementations of this interface manage insert operations for raw TWR entries,
 * which typically include device addresses, timestamps, and measured distances.
 * These measurements are used in the WideLoc system for distance estimation
 * and positioning analysis.
 */
interface TWRDataSource {

    /**
     * Inserts a single TWR data entry into the data source.
     *
     * @param twrData The [TWRDataEntity] to be inserted.
     */
    suspend fun insertTWRData(twrData: TWRDataEntity)

    /**
     * Inserts multiple TWR data entries in bulk.
     *
     * @param twrDatas A list of TWR data entities to be inserted.
     */
    suspend fun insertTWRDatas(twrDatas: List<TWRDataEntity>)
}
