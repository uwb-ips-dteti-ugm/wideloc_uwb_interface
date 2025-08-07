package com.rizqi.wideloc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rizqi.wideloc.data.local.dao.DeviceDao
import com.rizqi.wideloc.data.local.dao.MapDao
import com.rizqi.wideloc.data.local.dao.TWRDataDao
import com.rizqi.wideloc.data.local.dao.TrackingSessionDao
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.data.local.entity.DeviceTrackingHistoryEntity
import com.rizqi.wideloc.data.local.entity.DistanceEntity
import com.rizqi.wideloc.data.local.entity.DistancesWithTimestampEntity
import com.rizqi.wideloc.data.local.entity.LatencyEntity
import com.rizqi.wideloc.data.local.entity.MapEntity
import com.rizqi.wideloc.data.local.entity.PointEntity
import com.rizqi.wideloc.data.local.entity.PowerConsumptionEntity
import com.rizqi.wideloc.data.local.entity.TWRDataEntity
import com.rizqi.wideloc.data.local.entity.TrackingSessionEntity
import com.rizqi.wideloc.utils.Converters
import dagger.hilt.android.qualifiers.ApplicationContext

@Database(
    entities = [
        DeviceEntity::class,
        TWRDataEntity::class,
        MapEntity::class,
        TrackingSessionEntity::class,
        DistancesWithTimestampEntity::class,
        DistanceEntity::class,
        DeviceTrackingHistoryEntity::class,
        LatencyEntity::class,
        PowerConsumptionEntity::class,
        PointEntity::class
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class WideLocDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun twrDataDao(): TWRDataDao
    abstract fun mapDao(): MapDao
    abstract fun trackingSessionDao(): TrackingSessionDao

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

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tracking_sessions (
                        sessionId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        date TEXT NOT NULL,
                        elapsedTime INTEGER NOT NULL
                    )
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS distances_with_timestamp (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sessionId INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL,
                        FOREIGN KEY(sessionId) REFERENCES tracking_sessions(sessionId) ON DELETE CASCADE
                    )
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_distances_with_timestamp_sessionId ON distances_with_timestamp(sessionId)
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS distances (
                        id TEXT NOT NULL,
                        point1Id TEXT NOT NULL,
                        point2Id TEXT NOT NULL,
                        distance REAL NOT NULL,
                        timestamp INTEGER NOT NULL,
                        groupId INTEGER NOT NULL,
                        PRIMARY KEY (groupId, id),
                        FOREIGN KEY(groupId) REFERENCES distances_with_timestamp(id) ON DELETE CASCADE
                    )
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_distances_groupId ON distances(groupId)
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS device_tracking_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sessionId INTEGER NOT NULL,
                        deviceId TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        FOREIGN KEY(sessionId) REFERENCES tracking_sessions(sessionId) ON DELETE CASCADE
                    )
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_device_tracking_history_sessionId ON device_tracking_history(sessionId)
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_device_tracking_history_deviceId ON device_tracking_history(deviceId)
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS latencies (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sessionId INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL,
                        latency REAL NOT NULL,
                        FOREIGN KEY(sessionId) REFERENCES tracking_sessions(sessionId) ON DELETE CASCADE
                    )
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_latencies_sessionId ON latencies(sessionId)
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS power_consumptions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sessionId INTEGER NOT NULL,
                        powerMilliWatts REAL NOT NULL,
                        currentMicroAmps INTEGER NOT NULL,
                        startBatteryLevel INTEGER NOT NULL,
                        endBatteryLevel INTEGER NOT NULL,
                        batteryDrop INTEGER NOT NULL,
                        durationInMilliSeconds REAL NOT NULL,
                        timestamp INTEGER NOT NULL,
                        FOREIGN KEY(sessionId) REFERENCES tracking_sessions(sessionId) ON DELETE CASCADE
                    )
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_power_consumptions_sessionId ON power_consumptions(sessionId)
                    """
                        .trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS points (
                        id TEXT PRIMARY KEY NOT NULL,
                        x_id TEXT NOT NULL,
                        x_value REAL NOT NULL,
                        y_id TEXT NOT NULL,
                        y_value REAL NOT NULL
                    )
                    """
                        .trimIndent()
                )
            }
        }
    }

}
