package com.rizqi.wideloc.data.local

import com.rizqi.wideloc.data.local.entity.DeviceTrackingHistoryEntity
import com.rizqi.wideloc.data.local.entity.DistanceEntity
import com.rizqi.wideloc.data.local.entity.DistancesWithTimestampEntity
import com.rizqi.wideloc.data.local.entity.LatencyEntity
import com.rizqi.wideloc.data.local.entity.PointEntity
import com.rizqi.wideloc.data.local.entity.PowerConsumptionEntity
import com.rizqi.wideloc.data.local.entity.TrackingSessionEntity

interface TrackingSessionDataSource {

    suspend fun insertTrackingSession(
        session: TrackingSessionEntity,
        distancesWithTimestamp: List<Pair<DistancesWithTimestampEntity, List<DistanceEntity>>>,
        deviceHistories: List<DeviceTrackingHistoryEntity>,
        latencies: List<LatencyEntity>,
        powerConsumptions: List<PowerConsumptionEntity>,
        points: List<PointEntity>
    )

    suspend fun getLastSessionId() : Int?
}