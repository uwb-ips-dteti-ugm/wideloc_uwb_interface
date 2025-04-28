package com.rizqi.wideloc.data.repository

import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.data.websocket.WideLocSocketDataSource
import com.rizqi.wideloc.di.IoDispatcher
import com.rizqi.wideloc.domain.model.CalibrationData
import com.rizqi.wideloc.domain.model.TrackingData
import com.rizqi.wideloc.domain.repository.TrackingRepository
import com.rizqi.wideloc.utils.DomainDataMapper
import com.rizqi.wideloc.utils.DomainDataMapper.asCalibrationRequest
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrackingRepositoryImpl  @Inject constructor(
    private val socketDataSource: WideLocSocketDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : TrackingRepository {
    override fun observeWebSocket(): Flow<Result<String>> = socketDataSource.observeWebSocket().map { event ->
        when (event) {
            is WebSocket.Event.OnMessageReceived -> {
                Result.Success(event.toString())
            }
            is WebSocket.Event.OnConnectionOpened<*> -> {
                Result.Success(event.toString())
            }
            else -> Result.Error(errorMessage = event.toString())
        }
    }.flowOn(ioDispatcher)

    override fun observeInspection(): Flow<TrackingData> = flow {
        socketDataSource.observeWebSocket().collect { event ->
            if (event is WebSocket.Event.OnMessageReceived) {
                if (event.message is Message.Text) {
                    val text = event.message as Message.Text
                    val inspectionData =
                        DomainDataMapper.mapTrackingResponseTextToTrackingData(
                            text.value
                        )
                    inspectionData?.let { emit(it) }
                }
            }
        }
    }.flowOn(ioDispatcher)

    override suspend fun sendCalibration(calibrationData: CalibrationData) {
        socketDataSource.sendCalibration(
            calibrationData.asCalibrationRequest()
        )
    }
}