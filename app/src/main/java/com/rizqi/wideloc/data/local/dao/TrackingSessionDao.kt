package com.rizqi.wideloc.data.local.dao

import androidx.room.*
import com.rizqi.wideloc.data.local.entity.*
import java.time.LocalDateTime

/**
 * Data Access Object (DAO) for managing tracking sessions and their
 * related entities in the WideLoc system.
 *
 * A tracking session consists of multiple data components such as:
 * - Distance measurements
 * - Timestamped grouped distances
 * - Device tracking histories
 * - Latency records
 * - Power consumption data
 * - Position points
 *
 * This DAO provides granular insertion functions for each entity as well
 * as a transactional method to persist a complete tracking session along
 * with all of its associated data.
 */
@Dao
interface TrackingSessionDao {

    /**
     * Inserts a single tracking session record.
     *
     * @param session the tracking session entity to insert.
     * @return the generated ID of the newly inserted session.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrackingSessionEntity): Long

    /**
     * Inserts timestamped distance groups for a session.
     *
     * Each [DistancesWithTimestampEntity] represents a group of distance
     * measurements recorded at a specific timestamp.
     *
     * @param distances the list of grouped timestamped distances.
     * @return a list of generated IDs for each inserted group.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistancesWithTimestamp(distances: List<DistancesWithTimestampEntity>): List<Long>

    /**
     * Inserts individual distance records.
     *
     * Each [DistanceEntity] must reference a valid distance group ID.
     *
     * @param distances the list of distance entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistanceEntities(distances: List<DistanceEntity>)

    /**
     * Inserts device tracking history records for a session.
     *
     * These records contain positional and metadata history for each device.
     *
     * @param histories the list of device tracking history entities.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceTrackingHistories(histories: List<DeviceTrackingHistoryEntity>)

    /**
     * Inserts latency measurement records associated with a tracking session.
     *
     * @param latencies the list of latency entities.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLatencyEntities(latencies: List<LatencyEntity>)

    /**
     * Inserts power consumption records for the devices during a session.
     *
     * @param consumptions the list of power consumption entities.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPowerConsumptions(consumptions: List<PowerConsumptionEntity>)

    /**
     * Inserts position points computed or recorded during a tracking session.
     *
     * @param points the list of point entities.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<PointEntity>)

    /**
     * Inserts a full tracking session and all related data in a single transaction.
     *
     * This method ensures atomicity: all components of the session are saved
     * together, preventing partial data insertion.
     *
     * ### Process Overview
     * 1. Insert the session and obtain its `sessionId`.
     * 2. Insert all timestamped distance groups, assigning the sessionId.
     * 3. Insert all distance records, linking them to their respective group IDs.
     * 4. Insert device tracking histories tied to this session.
     * 5. Insert latency and power consumption records.
     * 6. Insert all position points.
     *
     * @param session the main tracking session entity.
     * @param distancesWithTimestamp list of pairs containing:
     *   - a [DistancesWithTimestampEntity] (distance group)
     *   - a list of [DistanceEntity] belonging to that group
     * @param deviceHistories device tracking history records.
     * @param latencies latency measurement entries.
     * @param powerConsumptions device power consumption entries.
     * @param points position point entries.
     */
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

    /**
     * Retrieves the most recently created tracking session ID.
     *
     * @return the latest session ID, or `null` if no sessions exist.
     */
    @Query("SELECT MAX(sessionId) FROM tracking_sessions")
    suspend fun getLastSessionId(): Int?
}
