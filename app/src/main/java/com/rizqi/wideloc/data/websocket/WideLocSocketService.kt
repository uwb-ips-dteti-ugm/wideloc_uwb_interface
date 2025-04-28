package com.rizqi.wideloc.data.websocket

import com.rizqi.wideloc.data.websocket.request.CalibrationRequest
import com.rizqi.wideloc.data.websocket.response.TrackingResponse
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow

interface WideLocSocketService {

    @Receive
    fun observeWebSocket(): Flow<WebSocket.Event>

    @Receive
    fun observeTracking(): Flow<TrackingResponse>

    @Send
    fun sendCalibration(calibrationRequest: CalibrationRequest)
}