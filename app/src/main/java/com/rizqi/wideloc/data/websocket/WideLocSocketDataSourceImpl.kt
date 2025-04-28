package com.rizqi.wideloc.data.websocket

import com.rizqi.wideloc.data.websocket.request.CalibrationRequest
import com.rizqi.wideloc.data.websocket.response.TrackingResponse
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WideLocSocketDataSourceImpl @Inject constructor(
    private val socketService: WideLocSocketService
) : WideLocSocketDataSource{
    override fun observeWebSocket(): Flow<WebSocket.Event> = socketService.observeWebSocket()

    override fun observeTracking(): Flow<TrackingResponse> = socketService.observeTracking()

    override suspend fun sendCalibration(calibrationRequest: CalibrationRequest) {
        socketService.sendCalibration(calibrationRequest)
    }
}