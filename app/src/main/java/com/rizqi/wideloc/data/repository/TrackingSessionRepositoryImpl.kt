package com.rizqi.wideloc.data.repository

import com.rizqi.wideloc.data.local.TrackingSessionDataSource
import com.rizqi.wideloc.domain.model.TrackingSessionData
import com.rizqi.wideloc.domain.repository.TrackingSessionRepository
import com.rizqi.wideloc.utils.DomainDataMapper.toEntities
import com.rizqi.wideloc.utils.DomainDataMapper.toEntity
import javax.inject.Inject

class TrackingSessionRepositoryImpl @Inject constructor(
    private val trackingSessionDataSource: TrackingSessionDataSource
) :  TrackingSessionRepository{
    override suspend fun insertTrackingSession(trackingSessionData: TrackingSessionData) {
        val sessionEntity = trackingSessionData.toEntity()
        val sessionId = sessionEntity.sessionId

        trackingSessionDataSource.insertTrackingSession(
            session = sessionEntity,
            distancesWithTimestamp = trackingSessionData.recordedDistances.map {
                it.toEntities(sessionId)
            },
            deviceHistories = trackingSessionData.deviceTrackingHistoryData.map {
                it.toEntity(sessionId)
            },
            latencies = trackingSessionData.latencies.map {
                it.toEntity(sessionId)
            },
            powerConsumptions = trackingSessionData.powerConsumptions.map {
                it.toEntity(sessionId)
            },
            points = trackingSessionData.deviceTrackingHistoryData
                .flatMap { it.points }
                .distinctBy { it.id }
                .map { it.toEntity() }
        )
    }

    override suspend fun getNextSessionId(): Int {
        return (trackingSessionDataSource.getLastSessionId() ?: 0) + 1
    }
}