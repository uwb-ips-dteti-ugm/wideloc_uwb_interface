package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device

import android.content.Context
import android.net.wifi.WifiInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqi.wideloc.R
import com.rizqi.wideloc.usecase.DeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.data.local.entity.DeviceRole
import com.rizqi.wideloc.data.local.entity.UWBMode
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.DeviceOffsetData
import com.rizqi.wideloc.domain.model.UWBConfigData
import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.model.WifiConnectData
import com.rizqi.wideloc.domain.model.WifiProtocolData
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters.WifiInformation
import com.rizqi.wideloc.usecase.ConfigUWBUseCase
import com.rizqi.wideloc.usecase.ConfigWifiUseCase
import com.rizqi.wideloc.usecase.ConnectWifiUseCase
import com.rizqi.wideloc.usecase.GenerateIDInteractor
import com.rizqi.wideloc.usecase.GenerateIDUseCase
import com.rizqi.wideloc.utils.Constants
import com.rizqi.wideloc.utils.DomainDataMapper.asWifiProtocolEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val deviceUseCase: DeviceUseCase,
    private val configWifiUseCase: ConfigWifiUseCase,
    private val connectWifiUseCase: ConnectWifiUseCase,
    private val configUWBUseCase: ConfigUWBUseCase,
) : ViewModel() {

    private val generateIDUseCase: GenerateIDUseCase = GenerateIDInteractor()

    private val _id = MutableLiveData<String>()
    val id: LiveData<String> get() = _id

    private val _availableServers = MutableLiveData<List<DeviceData>>()
    val availableServers: LiveData<List<DeviceData>> get() = _availableServers

    private val _isAnyServerExist = MutableLiveData<Boolean>()
    val isAnyServerExist: LiveData<Boolean> get() = _isAnyServerExist

    private val _wifiInformation = MutableLiveData<WifiInformation?>()
    val wifiInformation: LiveData<WifiInformation?> get() = _wifiInformation

    private val _connectedWifiInfo = MutableLiveData<WifiInfo?>()
    val connectedWifiInfo: LiveData<WifiInfo?> get() = _connectedWifiInfo

    private val _connectedWifiInfoError = MutableLiveData<String?>()
    val connectedWifiInfoError: LiveData<String?> get() = _connectedWifiInfoError

    private val _deviceSetupModel = MutableLiveData<DeviceSetupModel>(DeviceSetupModel())
    val deviceSetupModel: LiveData<DeviceSetupModel> get() = _deviceSetupModel

    private val _nameValidationResult = MutableLiveData<Result<Boolean>?>()
    val nameValidationResult: LiveData<Result<Boolean>?> get() = _nameValidationResult

    private val _saveDeviceResult = MutableLiveData<Result<Boolean>?>()
    val saveDeviceResult: LiveData<Result<Boolean>?> get() = _saveDeviceResult

    private val _networkConfig = MutableLiveData<NetworkConfig>(NetworkConfig())
    val networkConfig: LiveData<NetworkConfig> get() = _networkConfig

    private val _uwbConfig = MutableLiveData<UWBConfig>(UWBConfig())
    val uwbConfig: LiveData<UWBConfig> get() = _uwbConfig

    private val _networkConfigError = MutableLiveData<NetworkConfigError?>(NetworkConfigError())
    val networkConfigError: LiveData<NetworkConfigError?> get() = _networkConfigError

    private val _uwbConfigError = MutableLiveData<UWBConfigError?>(UWBConfigError())
    val uwbConfigError: LiveData<UWBConfigError?> get() = _uwbConfigError

    private val _configNetworkResult = MutableLiveData<Result<Boolean>?>()
    val configNetworkResult: LiveData<Result<Boolean>?> get() = _configNetworkResult

    private val _configUWBResult = MutableLiveData<Result<Boolean>?>()
    val configUWBResult: LiveData<Result<Boolean>?> get() = _configUWBResult

    private var savedDeviceData: DeviceData? = null

    private val _jumpToPage = MutableLiveData<Int?>()
    val jumpTopage: LiveData<Int?> get() = _jumpToPage

    init {
        _id.value = generateIDUseCase.invoke()
        viewModelScope.launch {
            _isAnyServerExist.value = deviceUseCase.isAnyServerSaved()
            _availableServers.value = deviceUseCase.getByRole(DeviceRole.Server)
        }
    }

    fun setWifiInformation(wifiInformation: WifiInformation?) {
        _wifiInformation.postValue(wifiInformation)
    }

    fun setDeviceSetup(
        name: String,
        offsetX: String? = null,
        offsetY: String? = null,
        offsetZ: String? = null,
        role: DeviceRole? = null,
        imagePath: String? = null,
    ) {
        val deviceSetupModelBefore = _deviceSetupModel.value
        val deviceSetupModelAfter = deviceSetupModelBefore?.copy(
            name = name,
            offsetX = offsetX?.toDoubleOrNull() ?: deviceSetupModelBefore.offsetX,
            offsetY = offsetY?.toDoubleOrNull() ?: deviceSetupModelBefore.offsetY,
            offsetZ = offsetZ?.toDoubleOrNull() ?: deviceSetupModelBefore.offsetZ,
            role = role ?: deviceSetupModelBefore.role,
            imagePath = imagePath ?: deviceSetupModelBefore.imagePath,
        )

        _deviceSetupModel.value = deviceSetupModelAfter ?: deviceSetupModelBefore
        if (deviceSetupModelAfter?.name?.isBlank() == true) {
            _nameValidationResult.value = Result.Error("Name can't be blank")
            return
        } else {
            _nameValidationResult.value = Result.Success(true)
        }

        _deviceSetupModel.value?.let {
            _saveDeviceResult.value = Result.Success(true)
        }

    }

    private fun saveDeviceToDatabase(model: DeviceSetupModel, networkConfig: NetworkConfig) {
        val protocol = WifiProtocolData(
            port = networkConfig.port,
            mdns = networkConfig.dns,
            autoConnect = networkConfig.isAutoConnect,
            deviceAccessPointSSID = networkConfig.apSSID,
            deviceAccessPointPPassword = networkConfig.apPassword,
            networkSSID = networkConfig.staSSID,
            networkPassword = networkConfig.staPassword
        )
        val deviceData = DeviceData(
            id = id.value ?: generateIDUseCase.invoke(),
            name = model.name,
            imageUrl = model.imagePath ?: "",
            role = model.role,
            offset = DeviceOffsetData(x = model.offsetX, y = model.offsetY, z = model.offsetZ),
            protocol = protocol,
            isAvailable = false,
            lastConnectedAt = null,
            createdAt = LocalDateTime.now(),
            uwbConfigData = null
        )
        savedDeviceData = deviceData
        viewModelScope.launch {
            try {
                _saveDeviceResult.value = Result.Loading()
                deviceUseCase.insertDevice(deviceData)
                _saveDeviceResult.value = Result.Success(true)
            } catch (e: Exception){
                _saveDeviceResult.value = Result.Error(e.message ?: "Failed to save")
            }
        }
    }

    private fun updateDeviceUWBConfigInDatabase(uwbConfigData: UWBConfigData){
        if (savedDeviceData == null) throw return

        savedDeviceData = savedDeviceData!!.copy(
            uwbConfigData = uwbConfigData,
            isAvailable = true,
        )

        viewModelScope.launch {
            try {
                _saveDeviceResult.value = Result.Loading()
                deviceUseCase.insertDevice(savedDeviceData!!)
                _saveDeviceResult.value = Result.Success(true)
            } catch (e: Exception){
                _saveDeviceResult.value = Result.Error(e.message ?: "Failed to save")
            }
        }
    }

    fun resetAll() {
        _wifiInformation.value = null
        _connectedWifiInfo.value = null
        _connectedWifiInfoError.value = null
        _deviceSetupModel.value = DeviceSetupModel()
        _nameValidationResult.value = null
        _networkConfig.value = NetworkConfig()
        _networkConfigError.value = null
        _uwbConfig.value = UWBConfig()
    }

    fun setConnectedWifi(wifiInfo: WifiInfo?) {
        _connectedWifiInfo.value = wifiInfo
        _connectedWifiInfoError.value = null
        if (wifiInformation.value == null) {
           _connectedWifiInfoError.value =  context.getString(R.string.you_have_not_selected_a_uwb_network_before)
        }
        if (!networkConfig.value?.staSSID.isNullOrBlank() && (wifiInfo?.ssid?.replace("\"", "") != networkConfig.value?.staSSID) ) {
            _connectedWifiInfoError.value = context.getString(
                R.string.please_connect_to_the_same_network_as_the_uwb_network,
                networkConfig.value?.staSSID
            )
        }
        if (networkConfig.value?.staSSID.isNullOrBlank() && (wifiInfo?.ssid?.replace("\"", "") != wifiInformation.value?.ssid)) {
            context.getString(
                R.string.network_is_different_from_the_one_selected,
                wifiInformation.value?.ssid
            )
        }
    }

    fun setDNS(dns: String){
        _networkConfig.value = networkConfig.value?.copy(dns = dns)
    }

    fun setPort(port: String){
        _networkConfig.value = networkConfig.value?.copy(port = port.toIntOrNull() ?: 80)
    }

    fun setAPSSID(ssid: String){
        _networkConfig.value = networkConfig.value?.copy(apSSID = ssid)
    }

    fun setIsApSSIDSameAsDNS(isSame: Boolean){
        _networkConfig.value = networkConfig.value?.copy(isApSSIDSameAsDNS = isSame)
        if (isSame) {
            setAPSSID(networkConfig.value?.dns ?: "")
        }
    }

    fun setAPPassword(password: String){
        _networkConfig.value = networkConfig.value?.copy(apPassword = password)
    }

    fun setStaSSID(ssid: String){
        _networkConfig.value = networkConfig.value?.copy(staSSID = ssid)
    }

    fun setStaPassword(password: String){
        _networkConfig.value = networkConfig.value?.copy(staPassword = password)
    }

    fun setAutoConnect(isAutoConnect: Boolean){
        _networkConfig.value = networkConfig.value?.copy(isAutoConnect = isAutoConnect)
    }

    fun setServer(server: DeviceData){
        _uwbConfig.value = uwbConfig.value?.copy(server = server)
    }

    fun getNetworkAddress(callback: (Int) -> Unit){
        viewModelScope.launch {
            val address = deviceUseCase.generateNetworkAddress()
            callback(address)
        }
    }

    fun getDeviceAddress(callback: (Int) -> Unit){
        viewModelScope.launch {
            val address = deviceUseCase.generateDeviceAddress()
            callback(address)
        }
    }
    
    fun configureNetwork(){
        val isConfigValid = validateNetworkConfig()
        if (!isConfigValid) return

        val validConfig = (networkConfig.value ?: NetworkConfig()).copy(
            dns = "${getNamePrefix()}${networkConfig.value?.dns}",
            apSSID = "${getNamePrefix()}${networkConfig.value?.apSSID}",
        )

        val wifiConfigData = WifiConfigData(
            port = validConfig.port,
            mdns = validConfig.dns
        )
        val wifiConnectData = WifiConnectData(
            autoConnect = validConfig.isAutoConnect,
            apSSID = validConfig.apSSID,
            apPassword = validConfig.apPassword,
            staSSID = validConfig.staSSID,
            staPassword = validConfig.staPassword
        )

        viewModelScope.launch {
            try {
                _configNetworkResult.value = Result.Loading()
                val configResult = configWifiUseCase.invoke(wifiConfigData)
                if (configResult){
                    val connectResult = connectWifiUseCase.invoke(wifiConnectData)
                    if (connectResult) {
                        _configNetworkResult.value = Result.Success(true)
                        deviceSetupModel.value?.let {
                            saveDeviceToDatabase(it , validConfig)
                        }
                    } else {
                        _configNetworkResult.value = Result.Error("Something wrong! Failed to configure uwb network")
                    }
                } else {
                    _configNetworkResult.value = Result.Error("Something wrong! Failed to configure uwb network")
                }
            } catch (e: Exception){
                _configNetworkResult.value = Result.Error(e.message ?: "Failed to configure uwb network")
            }
        }
    }

    private fun validateNetworkConfig(): Boolean {
        val dnsError = if(networkConfig.value?.dns.isNullOrBlank()) context.getString(R.string.please_fill_the_dns) else null
        val portError = if(networkConfig.value?.port == null) context.getString(R.string.please_fill_the_port) else null
        val apSSIDError = if(networkConfig.value?.apSSID.isNullOrBlank()) context.getString(R.string.please_fill_the_access_point_ssid) else null
        val apPasswordError = if(networkConfig.value?.apPassword.isNullOrBlank()) {
            context.getString(R.string.please_fill_the_access_point_password)
        } else if ((networkConfig.value?.apPassword?.length ?: 0) < 8) {
            context.getString(R.string.password_must_be_at_least_8_characters)
        } else {
            null
        }
        val staSSIDError = if(networkConfig.value?.staSSID.isNullOrBlank()) context.getString(R.string.please_fill_the_station_ssid) else null
        val staPasswordError = if(networkConfig.value?.staPassword.isNullOrBlank()) {
            context.getString(R.string.please_fill_the_station_password)
        } else if ((networkConfig.value?.staPassword?.length ?: 0) < 8) {
            context.getString(R.string.password_must_be_at_least_8_characters)
        } else {
            null
        }
        val newNetworkConfigError = NetworkConfigError(
            dns = dnsError,
            port = portError,
            apSSID = apSSIDError,
            apPassword = apPasswordError,
            staSSID = staSSIDError,
            staPassword = staPasswordError,
        )
        _networkConfigError.value = newNetworkConfigError
        return dnsError == null && portError == null && apSSIDError == null && apPasswordError == null && staSSIDError == null && staPasswordError == null
    }

    fun configureDevice(
        maxClient: String,
        networkAddress: String,
        deviceAddress: String,
        isAutoStart: Boolean,
    ){
        var autoGenerateNetworkAddress = 0
        var autoGenerateDeviceAddress = 0
        getNetworkAddress {
            autoGenerateNetworkAddress = it
        }
        getDeviceAddress {
            autoGenerateDeviceAddress = it
        }
        _uwbConfig.value = uwbConfig.value?.copy(
            client = maxClient.toIntOrNull() ?: 16,
            networkAddress = networkAddress.toIntOrNull() ?: autoGenerateNetworkAddress,
            deviceAddress = deviceAddress.toIntOrNull() ?: autoGenerateDeviceAddress,
            autoStart = isAutoStart
        )


        if (!validateUWBConfig()) return

        val validConfig = uwbConfig.value?.copy() ?: UWBConfig()

        val uwbConfigData = UWBConfigData(
            autoStart = validConfig.autoStart,
            isServer = deviceSetupModel.value?.role == DeviceRole.Server,
            maxClient = validConfig.client,
            mode = UWBMode.TWR,
            networkAddress = if (deviceSetupModel.value?.role == DeviceRole.Server){
                validConfig.networkAddress
            } else {
                validConfig.server?.uwbConfigData?.networkAddress ?: autoGenerateNetworkAddress
            },
            deviceAddress = validConfig.deviceAddress
        )

        viewModelScope.launch {
            try {
                _configUWBResult.value = Result.Loading()
                val configResult = configUWBUseCase.invoke(networkConfig.value?.dns ?: Constants.ESP_BASE_URL, uwbConfigData)
                if (configResult){
                    updateDeviceUWBConfigInDatabase(uwbConfigData)
                    _configUWBResult.value = Result.Success(true)
                } else {
                    _configUWBResult.value = Result.Error("Something wrong! Failed to configure uwb network")
                }
            } catch (e: Exception){
                _configUWBResult.value = Result.Error(e.message ?: "Failed to configure uwb network")
            }
        }

    }

    private fun validateUWBConfig(): Boolean {
        val uwbConfigError = UWBConfigError()

//        Server Role
        if (deviceSetupModel.value?.role == DeviceRole.Server){
            if ((uwbConfig.value?.client ?: 0) < 1) {
                uwbConfigError.client = "Client number can't be less than 1"
            }
            if ((uwbConfig.value?.networkAddress ?: 0) < 1) {
                uwbConfigError.networkAddress = "Network address can't be less than 1"
            }
            if ((uwbConfig.value?.deviceAddress ?: 0) < 1) {
                uwbConfigError.deviceAddress = "Device address can't be less than 1"
            }
        }
//        Anchor & Client Role
        else {
            if (uwbConfig.value?.server == null) {
                uwbConfigError.server = "Select a server, cause this device has role as client/anchor"
            }
            if ((uwbConfig.value?.deviceAddress ?: 0) < 1) {
                uwbConfigError.deviceAddress = "Device address can't be less than 1"
            }
        }
        _uwbConfigError.value = uwbConfigError
        return uwbConfigError.getIsValid()
    }

    fun getNamePrefix(): String {
        val role = deviceSetupModel.value?.role?.name ?: "role_unknown"
        val name = deviceSetupModel.value?.name ?: "name_unknown"
        val id = id.value ?: "id_unknown"
        return "$id-$role-$name-"
    }

    fun setDeviceData(deviceData: DeviceData){
        resetAll()
        val networkProtocol = deviceData.protocol.asWifiProtocolEntity()
        _id.value = deviceData.id
        _wifiInformation.value = WifiInformation(
            ssid = networkProtocol?.networkSSID ?: "",
            ipv4 = networkProtocol?.networkSSID ?: "",
            password = networkProtocol?.networkPassword ?: ""
        )
        _deviceSetupModel.value = DeviceSetupModel(
            name = deviceData.name,
            offsetX = deviceData.offset.x,
            offsetY = deviceData.offset.y,
            offsetZ = deviceData.offset.z,
            role = deviceData.role,
            imagePath = deviceData.imageUrl
        )
        networkProtocol?.let {
            _networkConfig.value = NetworkConfig(
                dns = it.mdns,
                port = it.port,
                apSSID = it.deviceAccessPointSSID,
                isApSSIDSameAsDNS = it.deviceAccessPointSSID == it.mdns,
                apPassword = it.deviceAccessPointPPassword,
                staSSID = it.networkSSID,
                staPassword = it.networkPassword,
                isAutoConnect = it.autoConnect
            )
        }
        deviceData.uwbConfigData?.let {
            _uwbConfig.value = UWBConfig(
                client = it.maxClient,
                networkAddress = it.networkAddress,
                deviceAddress = it.deviceAddress,
                server = availableServers.value?.find { server ->
                    server.uwbConfigData?.networkAddress == it.networkAddress
                },
                autoStart = it.autoStart
            )
        }
        savedDeviceData = deviceData
        if (deviceSetupModel.value != null && networkConfig.value != null) {
            _jumpToPage.value = 3
        }
    }

    fun hasJumpToPage(){
        _jumpToPage.value = null
    }

    data class DeviceSetupModel(
        val name: String = "",
        val offsetX: Double = 0.0,
        val offsetY: Double = 0.0,
        val offsetZ: Double = 0.0,
        val role: DeviceRole = DeviceRole.Server,
        val imagePath: String? = null,
    )

    data class NetworkConfig(
        val dns: String = "",
        val port: Int = 80,
        val apSSID: String = "",
        val isApSSIDSameAsDNS: Boolean = true,
        val apPassword: String = "",
        val staSSID: String = "",
        val staPassword: String = "",
        val isAutoConnect: Boolean = true,
    )

    data class NetworkConfigError(
        val dns: String? = null,
        val port: String? = null,
        val apSSID: String? = null,
        val apPassword: String? = null,
        val staSSID: String? = null,
        val staPassword: String? = null,
    )

    data class UWBConfig(
        val client: Int = 16,
        val networkAddress: Int = 0,
        val deviceAddress: Int = 1,
        val server: DeviceData? = null,
        val autoStart: Boolean = true,
    )

    data class UWBConfigError(
        var client: String? = null,
        var networkAddress: String? = null,
        var deviceAddress: String? = null,
        var server: String? = null,
    ) {
        fun getIsValid(): Boolean {
            return client == null && networkAddress == null && deviceAddress == null && server == null
        }
    }

}