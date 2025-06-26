package com.rizqi.wideloc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.rizqi.wideloc.data.local.entity.TWRDataEntity

@Dao
interface TWRDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(twrDataEntity: TWRDataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(twrDataEntities: List<TWRDataEntity>)
}