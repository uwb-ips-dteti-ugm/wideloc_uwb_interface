package com.rizqi.wideloc.data.websocket

import com.rizqi.wideloc.data.websocket.request.CalibrationRequest
import com.rizqi.wideloc.data.websocket.response.TrackingResponse
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.Flow

interface WideLocSocketDataSource {
    fun observeWebSocket(): Flow<WebSocket.Event>

    fun observeTracking(): Flow<TrackingResponse>

    suspend fun sendCalibration(calibrationRequest: CalibrationRequest)
}