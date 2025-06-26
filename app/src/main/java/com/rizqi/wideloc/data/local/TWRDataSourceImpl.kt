package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.dao.DeviceDao
import com.rizqi.wideloc.data.local.dao.TWRDataDao
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.DeviceRole
import com.rizqi.wideloc.data.local.entity.TWRDataEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TWRDataSourceImpl @Inject constructor(
    private val twrDataDao: TWRDataDao
) : TWRDataSource {
    override suspend fun insertTWRData(twrData: TWRDataEntity) {
        twrDataDao.insert(twrData)
    }

    override suspend fun insertTWRDatas(twrDatas: List<TWRDataEntity>) {
        twrDataDao.insertAll(twrDatas)
    }

}
