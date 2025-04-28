package com.rizqi.wideloc.utils

import com.google.gson.Gson
import com.rizqi.wideloc.data.websocket.request.CalibrationRequest
import com.rizqi.wideloc.data.websocket.response.TrackingResponse
import com.rizqi.wideloc.domain.model.CalibrationData
import com.rizqi.wideloc.domain.model.TrackingData

object DomainDataMapper {
    fun mapTrackingResponseTextToTrackingData(
        input: String
    ) : TrackingData? {
        return try {
            val response = Gson().fromJson(input, TrackingResponse::class.java)
            with(response) {
                TrackingData(
                    x = x,
                    y = y,
                    z = z,
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun CalibrationData.asCalibrationRequest(): CalibrationRequest {
        return CalibrationRequest(data = data)
    }
}