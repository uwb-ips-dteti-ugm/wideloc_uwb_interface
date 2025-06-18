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
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.DeviceOffsetData
import com.rizqi.wideloc.domain.model.WifiConfigData
import com.rizqi.wideloc.domain.model.WifiConnectData
import com.rizqi.wideloc.domain.model.WifiProtocolData
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters.WifiInformation
import com.rizqi.wideloc.usecase.ConfigWifiUseCase
import com.rizqi.wideloc.usecase.ConnectWifiUseCase
import com.rizqi.wideloc.usecase.GenerateIDInteractor
import com.rizqi.wideloc.usecase.GenerateIDUseCase
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
) : ViewModel() {

    private val generateIDUseCase: GenerateIDUseCase = GenerateIDInteractor()

    private val _id = MutableLiveData<String>()
    val id: LiveData<String> get() = _id

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

    private val _hostAddress = MutableLiveData<String?>()
    val hostAddress: LiveData<String?> get() = _hostAddress

    private val _networkConfig = MutableLiveData<NetworkConfig>(NetworkConfig())
    val networkConfig: LiveData<NetworkConfig> get() = _networkConfig

    private val _networkConfigError = MutableLiveData<NetworkConfigError?>(NetworkConfigError())
    val networkConfigError: LiveData<NetworkConfigError?> get() = _networkConfigError

    private val _configNetworkResult = MutableLiveData<Result<Boolean>?>()
    val configNetworkResult: LiveData<Result<Boolean>?> get() = _configNetworkResult

    init {
        _id.value = generateIDUseCase.invoke()
        viewModelScope.launch {
            _isAnyServerExist.value = deviceUseCase.isAnyServerSaved()
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

    fun resetAll() {
        _wifiInformation.value = null
        _connectedWifiInfo.value = null
        _connectedWifiInfoError.value = null
        _deviceSetupModel.value = DeviceSetupModel()
        _nameValidationResult.value = null
        _networkConfig.value = NetworkConfig()
        _networkConfigError.value = null
    }

    fun setConnectedWifi(wifiInfo: WifiInfo?) {
        _connectedWifiInfo.value = wifiInfo
        _connectedWifiInfoError.value = if (wifiInformation.value == null) {
            context.getString(R.string.you_have_not_selected_a_uwb_network_before)
        } else if (wifiInfo?.ssid?.replace("\"", "") != wifiInformation.value?.ssid) {
            context.getString(
                R.string.network_is_different_from_the_one_selected,
                wifiInformation.value?.ssid
            )
        } else {
            null
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

    fun getNamePrefix(): String {
        val role = deviceSetupModel.value?.role?.name ?: "role_unknown"
        val name = deviceSetupModel.value?.name ?: "name_unknown"
        val id = id.value ?: "id_unknown"
        return "$id-$role-$name-"
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

}