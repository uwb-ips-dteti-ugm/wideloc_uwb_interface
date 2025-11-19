package com.rizqi.wideloc.usecase

import com.google.gson.GsonBuilder
import com.rizqi.wideloc.data.local.TWRDataSource
import com.rizqi.wideloc.domain.model.DeviceCoordinate
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.DistancesWithTimestamp
import com.rizqi.wideloc.domain.model.LayoutInitialCoordinate
import com.rizqi.wideloc.domain.model.MapUnit
import com.rizqi.wideloc.domain.model.TWRData
import com.rizqi.wideloc.domain.model.TrackingSessionData
import com.rizqi.wideloc.domain.repository.DeviceRepository
import com.rizqi.wideloc.domain.repository.UWBDeviceRepository
import com.rizqi.wideloc.utils.DomainDataMapper.asWifiProtocolEntity
import com.rizqi.wideloc.utils.DomainDataMapper.toTWRDataEntity
import timber.log.Timber
import javax.inject.Inject

class GetUpdatedPositionInteractor @Inject constructor(
    private val twrDataSource: TWRDataSource,
    private val deviceRepository: DeviceRepository,
    private val uwbDeviceRepository: UWBDeviceRepository
) : GetUpdatedPositionUseCase {
    private val generateIDUseCase: GenerateIDUseCase = GenerateIDInteractor()
    private val newtonRaphsonUseCase: NewtonRaphsonUseCase = NewtonRaphsonInteractor()
    private val kalmanFilterUseCase: KalmanFilterUseCase = KalmanFilterInteractor()
    private val TAG = "GetUpdatedPosition"

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override suspend fun invoke(
        session: TrackingSessionData,
        server: DeviceData,
        anchors: List<DeviceData>,
        layoutInitialCoordinate: LayoutInitialCoordinate?,
        mapUnit: MapUnit,
    ): TrackingSessionData {
//        Get Raw Data
        val serverDNS = server.protocol.asWifiProtocolEntity()?.mdns
            ?: throw IllegalStateException("Server DNS is null")
        Timber.tag(TAG).d("[1] server dns:\n${gson.toJson(serverDNS)}")
        val rawTWRData = uwbDeviceRepository.getTWRData(serverDNS)
        Timber.tag(TAG).d("[2] raw twr data:\n${gson.toJson(rawTWRData)}")
        val twrDataEntities = rawTWRData.map { it.toTWRDataEntity(session.sessionId) }
        Timber.tag(TAG).d("[3] twr data entities:\n${gson.toJson(twrDataEntities)}")

//        Save Raw Data to Database
        twrDataSource.insertTWRDatas(twrDataEntities)

//        Cache devices by address to avoid repeated DB calls
        val devicesCache = mutableMapOf<Int, DeviceData?>()
        suspend fun getCachedDevice(address: Int): DeviceData? {
            return devicesCache.getOrPut(address) {
                deviceRepository.getDeviceByDeviceAddress(address)
            }
        }

        // Cache all TWR data by canonical point ID pairs
        val twrMap = mutableMapOf<Set<String>, TWRData>()
        for (twr in rawTWRData) {
            val d1 = getCachedDevice(twr.address1)
            val d2 = getCachedDevice(twr.address2)

            val id1 = d1?.getCorrespondingPointId() ?: continue
            val id2 = d2?.getCorrespondingPointId() ?: continue

            val key = setOf(id1, id2)
            val existing = twrMap[key]

            if (existing == null || twr.timestamp > existing.timestamp) {
                twrMap[key] = twr
            }
        }

//        Convert Raw to Domain Data
        val lastDistances = session.recordedDistances.last()
        val updatedDistancesList = lastDistances.distances.mapIndexed { index, distance ->
            val key = setOf(distance.point1.id, distance.point2.id)
            val matchingTWR = twrMap[key]

            Timber.tag(TAG).d("[4.$index] Matching TWR Check: ${gson.toJson(matchingTWR)}")

            matchingTWR?.let {
                distance.copy(
                    distance = it.distance,
                    timestamp = it.timestamp.toLong()
                )
            } ?: distance
        }
        val newDistanceRecord = DistancesWithTimestamp(
            distances = updatedDistancesList,
            timestamp = lastDistances.timestamp + 1,
        )
        Timber.tag(TAG).d("[5] initial distances:\n${gson.toJson(updatedDistancesList)}")
        Timber.tag(TAG).d("[6] updated distance with timestamps:\n${gson.toJson(newDistanceRecord)}")

//        Calculate the New Positions
        val fixedDevices = listOf(server) + anchors
        val fixedPointIds = fixedDevices.map { it.getCorrespondingPointId() }
        Timber.tag(TAG).d("[7] fixed point ids: \n${gson.toJson(fixedPointIds)}")
        val predictedPoints = newtonRaphsonUseCase.newtonRaphson(
            initialDistances = updatedDistancesList,
            fixedPointIds = fixedPointIds.toSet()
        )
        Timber.tag(TAG).d("[8] predictedPoints: \n${gson.toJson(predictedPoints)}")

//        Kalman Filter Correction
        val correctedPoints = predictedPoints.map {
            if(it.id in fixedPointIds){
                it
            } else {
                kalmanFilterUseCase.update(it)
            }
        }
        Timber.tag(TAG).d("[9] correctedPoints: \n${gson.toJson(correctedPoints)}")

//        Apply map offset after positions are acquired
        val deviceOffsets: List<DeviceCoordinate?> = listOf(
            layoutInitialCoordinate?.serverCoordinate,
            layoutInitialCoordinate?.anchorCoordinate
        ) + (layoutInitialCoordinate?.clientsCoordinate ?: emptyList())
        val offsetAppliedPoints = correctedPoints.map { point ->
            val pointOffset = deviceOffsets.find { deviceOffset ->
                deviceOffset?.deviceData?.getCorrespondingPointId() == point.id
            }
            val x = point.x
            val y = point.y
            point.copy(
                x = x.copy(
                    value = x.value + (pointOffset?.coordinate?.xOffset ?: 0.0)
                ),
                y = y.copy(
                    value = y.value + (pointOffset?.coordinate?.yOffset ?: 0.0)
                )
            )
        }
        Timber.tag(TAG).d("[10] offsetAppliedPoints: \n${gson.toJson(offsetAppliedPoints)}")

//        Return the Results
        val updatedDeviceHistory = session.deviceTrackingHistoryData.mapIndexed { index, history ->
            val pointId = history.deviceData.getCorrespondingPointId()
            val newPoint = offsetAppliedPoints.find { it.id == pointId }
            val updatedPoints = history.points + listOfNotNull(newPoint)
            Timber.tag(TAG).d("[11.${index}] new point for ${history.deviceData.id}: \n${gson.toJson(newPoint)}")

            history.copy(
                points = updatedPoints, timestamp = history.timestamp + 1
            )
        }
        Timber.tag(TAG).d("[12] updated device history: \n${gson.toJson(updatedDeviceHistory)}")
        session.apply {
            recordedDistances.add(newDistanceRecord)
            deviceTrackingHistoryData = updatedDeviceHistory.toMutableList()
        }
        return session
    }

    // helpers
    private fun toMeters(value: Double, unit: MapUnit): Double = when (unit) {
        MapUnit.MM -> value / 1000.0
        MapUnit.CM -> value / 100.0
        MapUnit.M  -> value
    }

    private fun fromMeters(valueMeters: Double, unit: MapUnit): Double = when (unit) {
        MapUnit.MM -> valueMeters * 1000.0
        MapUnit.CM -> valueMeters * 100.0
        MapUnit.M  -> valueMeters
    }
}