package com.rizqi.wideloc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rizqi.wideloc.data.local.dao.DeviceDao
import com.rizqi.wideloc.data.local.dao.MapDao
import com.rizqi.wideloc.data.local.dao.TWRDataDao
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.MapEntity
import com.rizqi.wideloc.data.local.entity.TWRDataEntity
import com.rizqi.wideloc.utils.Converters
import dagger.hilt.android.qualifiers.ApplicationContext

@Database(
    entities = [
        DeviceEntity::class,
        TWRDataEntity::class,
        MapEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class WideLocDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun twrDataDao(): TWRDataDao
    abstract fun mapDao(): MapDao

    companion object {
        @Volatile
        private var INSTANCE: WideLocDatabase? = null

        fun getInstance(@ApplicationContext context: Context): WideLocDatabase {
            return INSTANCE ?: synchronized(this) {
                val dbBuilder = Room.databaseBuilder(
                    context,
                    WideLocDatabase::class.java,
                    "wideloc.db"
                )
                val instance = dbBuilder.build()
                INSTANCE = instance
                instance
            }
        }
    }
}