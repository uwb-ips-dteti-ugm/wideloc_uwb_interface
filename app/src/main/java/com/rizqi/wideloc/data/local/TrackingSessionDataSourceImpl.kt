package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.dao.TrackingSessionDao
import com.rizqi.wideloc.data.local.entity.*
import javax.inject.Inject

class TrackingSessionDataSourceImpl @Inject constructor(
    private val trackingSessionDao: TrackingSessionDao
) : TrackingSessionDataSource {

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

    override suspend fun getLastSessionId(): Int? {
        return trackingSessionDao.getLastSessionId()
    }
}
