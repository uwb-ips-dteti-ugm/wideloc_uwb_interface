package com.rizqi.wideloc.usecase

import android.util.Log
import com.google.gson.GsonBuilder
import com.rizqi.wideloc.data.local.TWRDataSource
import com.rizqi.wideloc.data.repository.FakeUWBDeviceRepository
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.Point
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
) : GetUpdatedPositionUseCase {
    private val generateIDUseCase: GenerateIDUseCase = GenerateIDInteractor()
    private val newtonRaphsonUseCase: NewtonRaphsonUseCase = NewtonRaphsonInteractor()
    private val uwbDeviceRepository: UWBDeviceRepository = FakeUWBDeviceRepository()
    private val TAG = "GetUpdatedPosition"

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override suspend fun invoke(
        session: TrackingSessionData,
        server: DeviceData,
        anchors: List<DeviceData>,
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

//        Convert Raw to Domain Data
        val lastDistances = session.recordedDistances.last()
        val updatedDistancesList = lastDistances.distances.mapIndexed { index, distance ->
            val matchingTWR = rawTWRData
                .filter { twr ->
                    val device1 = getCachedDevice(twr.address1)
                    val device2 = getCachedDevice(twr.address2)

                    Timber.tag(TAG).d("[4.${index}.1] Matching TWR Check:\n${gson.toJson(twr)}")
                    Timber.tag(TAG).d("[4.${index}.2] Device 1:\n${gson.toJson(device1)}")
                    Timber.tag(TAG).d("[4.${index}.3] Device 2:\n${gson.toJson(device2)}")

                    val id1 = device1?.getCorrespondingPointId()
                    val id2 = device2?.getCorrespondingPointId()

                    (distance.point1.id == id1 && distance.point2.id == id2) || (distance.point1.id == id2 && distance.point2.id == id1)
                }.maxByOrNull {
                    it.timestamp
                }

            matchingTWR?.let {
                distance.copy(
                    distance = it.distance, timestamp = it.timestamp.toLong()
                )
            } ?: distance
        }
        val updatedDistanceRecord = lastDistances.copy(
            distances = lastDistances.distances + updatedDistancesList,
            timestamp = lastDistances.timestamp + 1,
        )
        Timber.tag(TAG).d("[5] initial distances:\n${gson.toJson(updatedDistancesList)}")
        Timber.tag(TAG).d("[6] updated distance with timestamps:\n${gson.toJson(updatedDistanceRecord)}")

//        Calculate the New Positions
        val fixedDevices = listOf(server) + anchors
        val fixedPointIds = fixedDevices.map { it.getCorrespondingPointId() }
        Timber.tag(TAG).d("[7] fixed point ids: \n${gson.toJson(fixedPointIds)}")
        val predictedPoints = newtonRaphsonUseCase.newtonRaphson(
            initialDistances = updatedDistancesList,
            fixedPointIds = fixedPointIds.toSet()
        )
        Timber.tag(TAG).d("[8] predictedPoints: \n${gson.toJson(predictedPoints)}")

//        Return the Results
        val updatedDeviceHistory = session.deviceTrackingHistoryData.mapIndexed { index, history ->
            val pointId = history.deviceData.getCorrespondingPointId()
            val newPoint = predictedPoints.find { it.id == pointId }
            val updatedPoints = history.points + listOfNotNull(newPoint)
            Timber.tag(TAG).d("[9.${index}] new point for ${history.deviceData.id}: \n${gson.toJson(newPoint)}")

            history.copy(
                points = updatedPoints, timestamp = history.timestamp + 1
            )
        }
        Timber.tag(TAG).d("[10] updated device history: \n${gson.toJson(updatedDeviceHistory)}")
        return session.copy(
            recordedDistances = session.recordedDistances + updatedDistanceRecord,
            deviceTrackingHistoryData = updatedDeviceHistory
        )
    }
}