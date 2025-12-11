package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.dao.TWRDataDao
import com.rizqi.wideloc.data.local.entity.TWRDataEntity
import javax.inject.Inject

/**
 * Implementation of [TWRDataSource] backed by a Room-based [TWRDataDao].
 *
 * This class handles the insertion of raw Two-Way Ranging (TWR) measurement data
 * into the local database. These measurements typically include distance values,
 * participating device addresses, and timestamps, and are essential for UWB-based
 * ranging and localization computations within the WideLoc system.
 *
 * @property twrDataDao The DAO responsible for storing TWR-related records.
 */
class TWRDataSourceImpl @Inject constructor(
    private val twrDataDao: TWRDataDao
) : TWRDataSource {

    /**
     * @inheritdoc
     */
    override suspend fun insertTWRData(twrData: TWRDataEntity) {
        twrDataDao.insert(twrData)
    }

    /**
     * @inheritdoc
     */
    override suspend fun insertTWRDatas(twrDatas: List<TWRDataEntity>) {
        twrDataDao.insertAll(twrDatas)
    }
}
