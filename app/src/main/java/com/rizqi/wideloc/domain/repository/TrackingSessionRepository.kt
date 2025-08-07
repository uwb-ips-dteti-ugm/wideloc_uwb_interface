package com.rizqi.wideloc.domain.repository

import com.rizqi.wideloc.domain.model.TrackingSessionData

interface TrackingSessionRepository {
    suspend fun insertTrackingSession(trackingSessionData: TrackingSessionData)

    suspend fun getNextSessionId(): Int
}