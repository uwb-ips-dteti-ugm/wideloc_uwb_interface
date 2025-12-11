package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.entity.DeviceTrackingHistoryEntity
import com.rizqi.wideloc.data.local.entity.DistanceEntity
import com.rizqi.wideloc.data.local.entity.DistancesWithTimestampEntity
import com.rizqi.wideloc.data.local.entity.LatencyEntity
import com.rizqi.wideloc.data.local.entity.PointEntity
import com.rizqi.wideloc.data.local.entity.PowerConsumptionEntity
import com.rizqi.wideloc.data.local.entity.TrackingSessionEntity

/**
 * Abstraction for handling storage and retrieval of UWB tracking session data.
 *
 * A tracking session consists of multiple related datasets, such as:
 * - Distance measurements grouped by timestamps
 * - Device tracking history entries
 * - Latency logs
 * - Power consumption metrics
 * - Point coordinate metadata
 *
 * Implementations of this interface are responsible for saving a complete session
 * along with all nested entities, ensuring referential consistency.
 */
interface TrackingSessionDataSource {

    /**
     * Inserts a full tracking session and all associated datasets into the data source.
     *
     * This operation persists:
     * - The main [TrackingSessionEntity]
     * - A list of distance-groups, where each item contains:
     *   - A [DistancesWithTimestampEntity]
     *   - A list of [DistanceEntity] belonging to that timestamp group
     * - Device tracking histories
     * - Latency records
     * - Power consumption logs
     * - Spatial points associated with the session
     *
     * @param session The session metadata.
     * @param distancesWithTimestamp A list of timestamped distance groups.
     * @param deviceHistories Tracking history for individual devices.
     * @param latencies Captured communication latency records.
     * @param powerConsumptions Power usage metrics during the session.
     * @param points Spatial reference points used for positioning calculations.
     */
    suspend fun insertTrackingSession(
        session: TrackingSessionEntity,
        distancesWithTimestamp: List<Pair<DistancesWithTimestampEntity, List<DistanceEntity>>>,
        deviceHistories: List<DeviceTrackingHistoryEntity>,
        latencies: List<LatencyEntity>,
        powerConsumptions: List<PowerConsumptionEntity>,
        points: List<PointEntity>
    )

    /**
     * Returns the last (most recently inserted) tracking session ID, if available.
     *
     * Useful for associating subsequent measurements or logs with the most
     * recently active session.
     *
     * @return The latest session ID, or `null` if no sessions exist.
     */
    suspend fun getLastSessionId(): Int?
}
