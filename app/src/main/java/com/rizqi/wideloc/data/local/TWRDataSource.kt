package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.entity.TWRDataEntity

interface TWRDataSource {

    suspend fun insertTWRData(twrData: TWRDataEntity)

    suspend fun insertTWRDatas(twrDatas: List<TWRDataEntity>)

}
