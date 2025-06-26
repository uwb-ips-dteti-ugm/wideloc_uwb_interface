package com.rizqi.wideloc.usecase

import android.util.Log
import com.rizqi.wideloc.data.local.TWRDataSource
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.DeviceTrackingHistoryData
import com.rizqi.wideloc.domain.model.Distance
import com.rizqi.wideloc.domain.model.Point
import com.rizqi.wideloc.domain.repository.DeviceRepository
import com.rizqi.wideloc.domain.repository.UWBDeviceRepository
import com.rizqi.wideloc.utils.DomainDataMapper.asWifiProtocolEntity
import com.rizqi.wideloc.utils.DomainDataMapper.toTWRDataEntity
import javax.inject.Inject

class GetUpdatedPositionInteractor @Inject constructor(
    private val uwbDeviceRepository: UWBDeviceRepository,
    private val twrDataSource: TWRDataSource,
    private val deviceRepository: DeviceRepository,
) : GetUpdatedPositionUseCase {

    private val generateIDUseCase: GenerateIDUseCase = GenerateIDInteractor()
    private val newtonRaphsonUseCase: NewtonRaphsonUseCase = NewtonRaphsonInteractor()

    override suspend fun invoke(
        sessionId: Int,
        server: DeviceData,
        anchors: List<DeviceData>,
        deviceTrackingHistories: List<DeviceTrackingHistoryData>
    ): List<DeviceTrackingHistoryData> {
//        Get Raw Data
        val serverDNS = server.protocol.asWifiProtocolEntity()?.mdns
            ?: throw NullPointerException("Server dns is null")
        Log.e("GetUpdatedPosition", "serverDNS: $serverDNS")

        val rawTWRData = uwbDeviceRepository.getTWRData(serverDNS)
        Log.e("GetUpdatedPosition", "rawTWRData: $rawTWRData")

        val twrDataEntities = rawTWRData.map { it.toTWRDataEntity(sessionId) }
        Log.e("GetUpdatedPosition", "twrDataEntities: $twrDataEntities")

//        Save Raw Data to Database
        twrDataSource.insertTWRDatas(twrDataEntities)

//        Convert Raw to Domain Data
        val initialDistances = rawTWRData.mapNotNull { twrData ->
            val device1 = deviceRepository.getDeviceByNetworkAddress(twrData.address1)
            val device2 = deviceRepository.getDeviceByNetworkAddress(twrData.address2)
            Log.e("GetUpdatedPosition", "device1: $device1, device2: $device2")

            if (device1 == null || device2 == null) return@mapNotNull null

            val d = twrData.distance
            val timestamp = twrData.timestamp.toLong()

//          Point Device 1
            val device1HistoryData =
                deviceTrackingHistories.find { data -> data.deviceData.id == device1.id }
                    ?: return@mapNotNull null
            val lastDevice1Point = device1HistoryData.points.last()

//          Point Device 2
            val device2HistoryData =
                deviceTrackingHistories.find { data -> data.deviceData.id == device2.id }
                    ?: return@mapNotNull null
            val lastDevice2Point = device2HistoryData.points.last()

//          Distance
            val distance = Distance(
                id = generateIDUseCase.invoke(),
                point1 = lastDevice1Point,
                point2 = lastDevice2Point,
                distance = d,
                timestamp = timestamp
            )

            return@mapNotNull distance
        }
        Log.e("GetUpdatedPosition", "initialDistances: $initialDistances")

//        Calculate the New Positions
        val fixedDevices = listOf(server) + anchors
        val fixedPointIds = fixedDevices.map { it.getCorrespondingPointId() }
        Log.e("GetUpdatedPosition", "fixedPointIds: $fixedPointIds")

//        val predictedPoints = newtonRaphsonUseCase.newtonRaphson(
//            initialDistances = initialDistances,
//            fixedPointIds = fixedPointIds.toSet()
//        )
        val predictedPoints = listOf<Point>()
        Log.e("GetUpdatedPosition", "predictedPoints: $predictedPoints")

//        Save New Positions and Session to Database

//        Return the Results
        val updatedHistoryData = deviceTrackingHistories.map { history ->
            val pointId = history.deviceData.getCorrespondingPointId()
            val updatedDistances = history.distances + initialDistances.filter {
                it.point1.id == pointId || it.point2.id == pointId
            }
            Log.e("GetUpdatedPosition", "updatedDistances for ${history.deviceData.id}: $updatedDistances")

            val newPoint = predictedPoints.find { it.id == pointId }
            val updatedPoints = history.points + listOfNotNull(newPoint)
            Log.e("GetUpdatedPosition", "newPoint for ${history.deviceData.id}: $newPoint")

            history.copy(
                points = updatedPoints,
                distances = updatedDistances,
                timestamp = history.timestamp + 1
            )
        }
        return updatedHistoryData
    }
}