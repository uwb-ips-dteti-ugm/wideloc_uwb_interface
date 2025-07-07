package com.rizqi.wideloc.presentation.viewmodel

import android.content.Context
import android.net.wifi.WifiInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqi.wideloc.R
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.data.local.entity.DeviceRole
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.DeviceTrackingHistoryData
import com.rizqi.wideloc.domain.model.DistancesWithTimestamp
import com.rizqi.wideloc.domain.model.MapData
import com.rizqi.wideloc.domain.model.Point
import com.rizqi.wideloc.domain.model.TrackingSessionData
import com.rizqi.wideloc.domain.model.Variable
import com.rizqi.wideloc.domain.repository.DeviceRepository
import com.rizqi.wideloc.domain.repository.MapRepository
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters.WifiInformation
import com.rizqi.wideloc.usecase.GenerateDistanceCombinationInteractor
import com.rizqi.wideloc.usecase.GenerateDistanceCombinationUseCase
import com.rizqi.wideloc.usecase.GenerateIDInteractor
import com.rizqi.wideloc.usecase.GenerateIDUseCase
import com.rizqi.wideloc.usecase.GetUpdatedPositionUseCase
import com.rizqi.wideloc.utils.DomainDataMapper.asWifiProtocolEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val getUpdatedPositionUseCase: GetUpdatedPositionUseCase,
    private val deviceRepository: DeviceRepository,
    private val mapRepository: MapRepository,
) : ViewModel() {

    private val generateIDUseCase: GenerateIDUseCase = GenerateIDInteractor()
    private val generateDistanceCombination: GenerateDistanceCombinationUseCase =
        GenerateDistanceCombinationInteractor()

    private val _session = MutableLiveData<TrackingSessionData>()
    val session: LiveData<TrackingSessionData> get() = _session

    private val _observeResult = MutableLiveData<Result<TrackingSessionData?>?>(null)
    val observeResult: LiveData<Result<TrackingSessionData?>?> = _observeResult

    private val _serverList = MutableLiveData<List<DeviceData>>()
    val serverList: LiveData<List<DeviceData>> get() = _serverList

    private val _anchorList = MutableLiveData<List<DeviceData>>()
    val anchorList: LiveData<List<DeviceData>> get() = _anchorList

    private val _clientList = MutableLiveData<List<DeviceData>>()
    val clientList: LiveData<List<DeviceData>> get() = _clientList

    private val _selectDevicesResult = MutableLiveData<Result<Boolean>>()
    val selectDevicesResult: LiveData<Result<Boolean>> get() = _selectDevicesResult

    private var _selectedServer = MutableLiveData<DeviceData?>()
    val selectedServer: LiveData<DeviceData?> get() = _selectedServer

    private var selectedAnchor: DeviceData? = null
    private var selectedClients: List<DeviceData> = emptyList()

    private val _wifiInformation = MutableLiveData<WifiInformation?>()
    val wifiInformation: LiveData<WifiInformation?> get() = _wifiInformation

    private val _connectedWifiInfo = MutableLiveData<WifiInfo?>()
    val connectedWifiInfo: LiveData<WifiInfo?> get() = _connectedWifiInfo

    private val _connectedWifiInfoError = MutableLiveData<String?>()
    val connectedWifiInfoError: LiveData<String?> get() = _connectedWifiInfoError

    private val _availableMaps = MutableLiveData<List<MapData>>()
    val availableMaps: LiveData<List<MapData>> get() = _availableMaps

    private val _selectedMap = MutableLiveData<MapData>()
    val selectedMap: LiveData<MapData> get() = _selectedMap

    private val _mapTransform = MutableLiveData<MapTransform>()
    val mapTransform: LiveData<MapTransform> get() = _mapTransform

    val mapCombinedWithTransform = MediatorLiveData<Pair<MapData?, MapTransform?>>().apply {
        addSource(selectedMap) {value = it to mapTransform.value}
        addSource(mapTransform) {value = selectedMap.value to it}
    }

    private val _saveMapError = MutableLiveData<SaveMapError>(SaveMapError())
    val saveMapError: LiveData<SaveMapError> get() = _saveMapError

    private val _saveMapResult = MutableLiveData<Result<Boolean>>()
    val saveMapResult: LiveData<Result<Boolean>> get() = _saveMapResult

    private val _layoutInitialCoordinate = MutableLiveData<LayoutInitialCoordinate>()
    val layoutInitialCoordinate: LiveData<LayoutInitialCoordinate> get() = _layoutInitialCoordinate

    private val _saveDeviceLayout = MutableLiveData<Result<Boolean>>()
    val saveDeviceLayout: LiveData<Result<Boolean>> get() = _saveDeviceLayout

    init {
        viewModelScope.launch {

            _serverList.value = deviceRepository.getByRole(DeviceRole.Server)
                .filter {
                it.isAvailable && it.uwbConfigData != null
            }

            mapRepository.getAllMaps().collect{
                _availableMaps.value = it
            }

//            val devices = deviceRepository.getAllDevices().first { it.isNotEmpty() }
//            val distances = generateDistanceCombination.invoke(devices)
//            val deviceTrackingHistories = devices.map { device ->
//                DeviceTrackingHistoryData(
//                    deviceData = device,
//                    points = listOf(
//                        Point(
//                            id = device.getCorrespondingPointId(),
//                            x = Variable(
//                                id = device.getCorrespondingXId(),
//                                value = 0.0
//                            ),
//                            y = Variable(
//                                id = device.getCorrespondingYId(),
//                                value = 0.0
//                            ),
//                        )
//                    ),
//                    timestamp = 0
//                )
//            }
//            _session.value = TrackingSessionData(
//                sessionId = 0,
//                recordedDistances = listOf(
//                    DistancesWithTimestamp(
//                        timestamp = 0,
//                        distances = distances
//                    )
//                ),
//                deviceTrackingHistoryData = deviceTrackingHistories
//            )
        }

    }

    fun startObserveData() {
        viewModelScope.launch {
//            _observeResult.value = Result.Loading()
            val session = session.value ?: return@launch
            val server = deviceRepository.getFirstByRole(DeviceRole.Server)
            val anchors = deviceRepository.getByRole(DeviceRole.Anchor)
            if (server == null || anchors.isEmpty()) {
                return@launch
            }
//            try {
                val updatedSession = getUpdatedPositionUseCase.invoke(
                    session = session,
                    server = server,
                    anchors = anchors
                )
                _session.value = updatedSession
//                _observeResult.value = Result.Success(updatedSession)
//            } catch (e: Exception){
//                _observeResult.value = Result.Error(e.message.toString())
//            }
        }
    }

    fun setSelectedServer(server: DeviceData){
        _selectedServer.value = server
        viewModelScope.launch {
            server.uwbConfigData?.networkAddress?.let {
                _anchorList.value = deviceRepository.getAnchorsByNetworkAddress(
                    it
                )
                _clientList.value = deviceRepository.getClientsByNetworkAddress(
                    it
                )
            }
        }
    }

    fun setSelectedAnchor(anchor: DeviceData){
        selectedAnchor = anchor
    }

    fun setSelectedClients(clients: List<DeviceData>){
        selectedClients = clients
    }

    fun validateSelectedDevices() {
        _selectDevicesResult.value = Result.Loading()
        if (selectedServer == null) {
            _selectDevicesResult.value = Result.Error(context.getString(R.string.select_a_server_first))
            return
        }
        if (selectedAnchor == null) {
            _selectDevicesResult.value = Result.Error(context.getString(R.string.select_a_anchor_first))
            return
        }
        if (selectedClients.isEmpty()) {
            _selectDevicesResult.value = Result.Error(context.getString(R.string.select_clients_first))
            return
        }
        _selectDevicesResult.value = Result.Success(true)
    }

    fun setWifiInformation(wifiInformation: WifiInformation?) {
        _wifiInformation.postValue(wifiInformation)
    }

    fun setConnectedWifi(wifiInfo: WifiInfo?) {
        _connectedWifiInfo.value = wifiInfo

        val currentSSID = wifiInfo?.ssid?.replace("\"", "")
        val serverSSID = selectedServer.value?.protocol?.asWifiProtocolEntity()?.networkSSID

        _connectedWifiInfoError.value = when {
            currentSSID != serverSSID -> {
                context.getString(
                    R.string.please_connect_to_the_same_network_as_the_uwb_network,
                    serverSSID
                )
            }
            else -> null
        }
    }

    fun setSelectedMap(mapData: MapData){
        _selectedMap.value = mapData
    }

    fun insertMap(name: String, imagePath: String?){
        viewModelScope.launch {
            mapRepository.insertMap(
                MapData(
                    name = name,
                    imageUri = imagePath ?: "",
                )
            )
        }
    }

    fun saveMapSelection(
        lengthText: String,
        widthText: String,
        mapRotation: Float,
        isFlipX: Boolean,
    ) {
        _saveMapResult.value = Result.Loading()
        val error = SaveMapError()
        val length = lengthText.toDoubleOrNull()
        val width = widthText.toDoubleOrNull()

        if (length == null || length <= 0) {
            error.length = context.getString(R.string.length_can_t_be_empty_or_less_than_zero)
        }
        if (width == null || width <= 0) {
            error.width = context.getString(R.string.width_can_t_be_empty_or_less_than_zero)
        }
        if (selectedMap.value == null){
            error.map = context.getString(R.string.select_a_map_first)
        }

        _mapTransform.value = mapTransform.value?.copy(
            length = length!!,
            width = width!!,
            rotation = mapRotation,
            isFlipX = isFlipX,
        )
        _saveMapError.value = error
        if (!error.isValid()) {
            _saveMapResult.value = Result.Error(context.getString(R.string.there_are_some_invalid_input))
            return
        }

        _saveMapResult.value = Result.Success(true)

    }

    private fun updateCoordinate(
        target: CoordinateTarget,
        deviceData: DeviceData? = null,
        axis: String,
        value: String? = null,
        delta: Double? = null,
        isOffset: Boolean = true
    ) {
        val current = layoutInitialCoordinate.value ?: return

        val originalCoordinate = when (target) {
            CoordinateTarget.SERVER -> current.serverCoordinate.coordinate
            CoordinateTarget.ANCHOR -> current.anchorCoordinate.coordinate
            CoordinateTarget.MAP -> current.mapCoordinate
            CoordinateTarget.CLIENT -> current.clientsCoordinate.find {
                it.deviceData?.id == deviceData?.id
            }?.coordinate ?: return
        }

        val currentValue = when {
            axis == "x" && isOffset -> originalCoordinate.xOffset
            axis == "y" && isOffset -> originalCoordinate.yOffset
            axis == "x" && !isOffset -> originalCoordinate.x
            axis == "y" && !isOffset -> originalCoordinate.y
            else -> return
        }

        val newValue = when {
            value?.endsWith(".") ?: false -> return
            value != null -> value.toDoubleOrNull() ?: return
            delta != null -> currentValue + delta
            else -> return
        }

        val updatedCoordinate = when {
            axis == "x" && isOffset -> originalCoordinate.copy(xOffset = newValue)
            axis == "y" && isOffset -> originalCoordinate.copy(yOffset = newValue)
            axis == "x" && !isOffset -> originalCoordinate.copy(x = newValue)
            axis == "y" && !isOffset -> originalCoordinate.copy(y = newValue)
            else -> originalCoordinate
        }

        val updatedLayout = when (target) {
            CoordinateTarget.SERVER -> current.copy(
                serverCoordinate = current.serverCoordinate.copy(coordinate = updatedCoordinate)
            )
            CoordinateTarget.ANCHOR -> current.copy(
                anchorCoordinate = current.anchorCoordinate.copy(coordinate = updatedCoordinate)
            )
            CoordinateTarget.MAP -> current.copy(
                mapCoordinate = updatedCoordinate
            )
            CoordinateTarget.CLIENT -> {
                val updatedClients = current.clientsCoordinate.map {
                    if (it.deviceData?.id == deviceData?.id) {
                        it.copy(coordinate = updatedCoordinate)
                    } else {
                        it
                    }
                }
                current.copy(clientsCoordinate = updatedClients)
            }
        }

        _layoutInitialCoordinate.postValue(updatedLayout)
    }

    fun setX(target: CoordinateTarget, deviceData: DeviceData? = null, value: String? = null, delta: Double? = null, isOffset: Boolean = false) {
        updateCoordinate(
            target = target,
            deviceData = deviceData,
            axis = "x",
            value = value,
            delta = delta,
            isOffset = isOffset
        )
    }

    fun setY(target: CoordinateTarget, deviceData: DeviceData? = null, value: String? = null, delta: Double? = null, isOffset: Boolean = false) {
        updateCoordinate(
            target = target,
            deviceData = deviceData,
            axis = "y",
            value = value,
            delta = delta,
            isOffset = isOffset
        )
    }

    fun saveDeviceLayout(){
        _saveDeviceLayout.value = Result.Success(true)
        startObserveData()
    }

    fun initLayoutInitialCoordinate(){
        viewModelScope.launch {
            _layoutInitialCoordinate.value = LayoutInitialCoordinate(
                serverCoordinate = DeviceCoordinate(
                    deviceData = selectedServer.value,
                    coordinate = Coordinate()
                ),
                anchorCoordinate = DeviceCoordinate(
                    deviceData = selectedAnchor,
                    coordinate = Coordinate()
                ),
                mapCoordinate = Coordinate(),
                clientsCoordinate = selectedClients.map { client ->
                    DeviceCoordinate(
                        deviceData = client,
                        coordinate = Coordinate()
                    )
                }
            )
        }
    }

    data class MapTransform(
        val length: Double,
        val width: Double,
        val rotation: Float,
        val isFlipX: Boolean,
    )

    data class SaveMapError(
        var length: String? = null,
        var width: String? = null,
        var map: String? = null,
    ){
        fun isValid(): Boolean {
            return length == null && width == null && map == null
        }
    }

    data class Coordinate(
        val x: Double = 0.0,
        val xOffset: Double = 0.0,
        val y: Double = 0.0,
        val yOffset: Double = 0.0,
    ) {
        fun areContentsTheSame(other: Coordinate): Boolean {
            return this.x == other.x &&
                    this.xOffset == other.xOffset &&
                    this.y == other.y &&
                    this.yOffset == other.yOffset
        }
    }

    enum class CoordinateTarget {
        SERVER,
        ANCHOR,
        CLIENT,
        MAP
    }

    data class DeviceCoordinate(
        val deviceData: DeviceData?,
        val coordinate: Coordinate,
    )

    data class LayoutInitialCoordinate(
        val serverCoordinate: DeviceCoordinate,
        val anchorCoordinate: DeviceCoordinate,
        val mapCoordinate: Coordinate,
        val clientsCoordinate: List<DeviceCoordinate>
    )

}