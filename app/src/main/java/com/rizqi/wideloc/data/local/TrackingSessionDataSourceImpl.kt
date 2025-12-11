package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.dao.TrackingSessionDao
import com.rizqi.wideloc.data.local.entity.*
import javax.inject.Inject

/**
 * Implementation of [TrackingSessionDataSource] backed by a Room-based [TrackingSessionDao].
 *
 * This class orchestrates the insertion and retrieval of complete UWB tracking sessions.
 * It delegates complex multi-entity transactions—such as saving distance groups,
 * device tracking histories, latency logs, power consumption stats, and spatial points—
 * to the underlying DAO, ensuring atomic and consistent database operations.
 *
 * @property trackingSessionDao The DAO responsible for performing tracking session operations.
 */
class TrackingSessionDataSourceImpl @Inject constructor(
    private val trackingSessionDao: TrackingSessionDao
) : TrackingSessionDataSource {

    /**
     * @inheritdoc
     */
    override suspend fun insertTrackingSession(
        session: TrackingSessionEntity,
        distancesWithTimestamp: List<Pair<DistancesWithTimestampEntity, List<DistanceEntity>>>,
        deviceHistories: List<DeviceTrackingHistoryEntity>,
        latencies: List<LatencyEntity>,
        powerConsumptions: List<PowerConsumptionEntity>,
        points: List<PointEntity>
    ) {
        trackingSessionDao.insertFullTrackingSession(
            session = session,
            distancesWithTimestamp = distancesWithTimestamp,
            deviceHistories = deviceHistories,
            latencies = latencies,
            powerConsumptions = powerConsumptions,
            points = points
        )
    }

    /**
     * @inheritdoc
     */
    override suspend fun getLastSessionId(): Int? {
        return trackingSessionDao.getLastSessionId()
    }
}
