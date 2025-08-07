package com.rizqi.wideloc.data.local.dao

import androidx.room.*
import com.rizqi.wideloc.data.local.entity.*
import java.time.LocalDateTime

@Dao
interface TrackingSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrackingSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistancesWithTimestamp(distances: List<DistancesWithTimestampEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistanceEntities(distances: List<DistanceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceTrackingHistories(histories: List<DeviceTrackingHistoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLatencyEntities(latencies: List<LatencyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPowerConsumptions(consumptions: List<PowerConsumptionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<PointEntity>)

    @Transaction
    suspend fun insertFullTrackingSession(
        session: TrackingSessionEntity,
        distancesWithTimestamp: List<Pair<DistancesWithTimestampEntity, List<DistanceEntity>>>,
        deviceHistories: List<DeviceTrackingHistoryEntity>,
        latencies: List<LatencyEntity>,
        powerConsumptions: List<PowerConsumptionEntity>,
        points: List<PointEntity>
    ) {
        val sessionId = insertSession(session).toInt()

        val distancesWithIds = distancesWithTimestamp.map { it.first.copy(sessionId = sessionId) }
        val groupIds = insertDistancesWithTimestamp(distancesWithIds)

        val distances = distancesWithTimestamp.flatMapIndexed { index, pair ->
            pair.second.map { it.copy(groupId = groupIds[index].toInt()) }
        }

        insertDistanceEntities(distances)

        insertDeviceTrackingHistories(deviceHistories.map { it.copy(sessionId = sessionId) })
        insertLatencyEntities(latencies.map { it.copy(sessionId = sessionId) })
        insertPowerConsumptions(powerConsumptions.map { it.copy(sessionId = sessionId) })
        insertPoints(points)
    }

    @Query("SELECT MAX(sessionId) FROM tracking_sessions")
    suspend fun getLastSessionId(): Int?
}