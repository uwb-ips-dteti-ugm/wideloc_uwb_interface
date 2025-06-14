package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device

import android.content.Context
import android.net.wifi.WifiInfo
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
import com.rizqi.wideloc.domain.model.BluetoothProtocolData
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.DeviceOffsetData
import com.rizqi.wideloc.domain.model.ProtocolData
import com.rizqi.wideloc.domain.model.WifiProtocolData
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters.WifiInformation
import com.rizqi.wideloc.usecase.GenerateIDInteractor
import com.rizqi.wideloc.usecase.GenerateIDUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val deviceUseCase: DeviceUseCase,
    @ApplicationContext
    private val context: Context,
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

    init {
        _id.value = generateIDUseCase.invoke()
        viewModelScope.launch {
            _isAnyServerExist.value = deviceUseCase.isAnyServerSaved()
        }
    }

    fun setWifiInformation(wifiInformation: WifiInformation?) {
        _wifiInformation.value = wifiInformation
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
//            saveDeviceToDatabase(it)
            _saveDeviceResult.value = Result.Success(true)
        }

    }

//    private fun saveDeviceToDatabase(model: DeviceSetupModel) {
//        var protocol = ProtocolData()
//        if (!url.value.isNullOrBlank()){
//            protocol = WifiProtocolData()
//        } else if (!hostAddress.value.isNullOrBlank()){
//            protocol = BluetoothProtocolData(
//                hostId = "",
//                hostAddress = hostAddress.value!!
//            )
//        }
//        val deviceData = DeviceData(
//            id = UUID.randomUUID().toString(),
//            name = model.name,
//            imageUrl = model.imagePath ?: "",
//            role = model.role,
//            offset = DeviceOffsetData(x = model.offsetX, y = model.offsetY, z = model.offsetZ),
//            protocol = protocol,
//            isAvailable = false,
//            lastConnectedAt = null,
//            createdAt = LocalDateTime.now()
//        )
//        viewModelScope.launch {
//            try {
//                deviceUseCase.insertDevice(deviceData)
//                _saveDeviceResult.value = Result.Success(true)
//            } catch (e: Exception){
//                _saveDeviceResult.value = Result.Error(e.message ?: "Failed to save")
//            }
//        }
//    }

    fun resetAll() {
        _wifiInformation.value = null
        _deviceSetupModel.value = DeviceSetupModel()
        _nameValidationResult.value = null
    }

    fun setConnectedWifi(wifiInfo: WifiInfo?) {
        _connectedWifiInfo.value = wifiInfo
        _connectedWifiInfoError.value = if (wifiInformation.value == null) {
            context.getString(R.string.you_have_not_selected_a_uwb_network_before)
        } else if (wifiInfo?.ssid != wifiInformation.value?.ssid) {
            context.getString(
                R.string.network_is_different_from_the_one_selected,
                wifiInformation.value?.ssid
            )
        } else {
            null
        }
    }

    data class DeviceSetupModel(
        val name: String = "",
        val offsetX: Double = 0.0,
        val offsetY: Double = 0.0,
        val offsetZ: Double = 0.0,
        val role: DeviceRole = DeviceRole.Server,
        val imagePath: String? = null,
    )

}