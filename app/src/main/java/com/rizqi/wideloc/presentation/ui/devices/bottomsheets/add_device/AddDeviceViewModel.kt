package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqi.wideloc.usecase.DeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.domain.model.BluetoothProtocolData
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.DeviceOffsetData
import com.rizqi.wideloc.domain.model.ProtocolData
import com.rizqi.wideloc.domain.model.WifiProtocolData
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val deviceUseCase: DeviceUseCase
) : ViewModel() {

    private val _url = MutableLiveData<String?>()
    val url: LiveData<String?> get() = _url

    private val _deviceSetupModel = MutableLiveData<DeviceSetupModel>(DeviceSetupModel())
    val deviceSetupModel: LiveData<DeviceSetupModel> get() = _deviceSetupModel

    private val _urlValidationResult = MutableLiveData<Result<Boolean>?>()
    val urlValidationResult: LiveData<Result<Boolean>?> get() = _urlValidationResult

    private val _nameValidationResult = MutableLiveData<Result<Boolean>?>()
    val nameValidationResult: LiveData<Result<Boolean>?> get() = _nameValidationResult

    private val _saveDeviceResult = MutableLiveData<Result<Boolean>?>()
    val saveDeviceResult: LiveData<Result<Boolean>?> get() = _saveDeviceResult

    private val _hostAddress = MutableLiveData<String?>()
    val hostAddress: LiveData<String?> get() = _hostAddress

    fun setUrl(newUrl: String) {
        deviceUseCase.validateSocketUrl(newUrl).also {
            if (it is Result.Success) {
                _url.value = newUrl
            }
            _urlValidationResult.value = it
        }
    }

    fun setDeviceSetup(
        name: String,
        offsetX: String? = null,
        offsetY: String? = null,
        offsetZ: String? = null,
        role: String? = null,
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

        _deviceSetupModel.value?.let { saveDeviceToDatabase(it) }

    }

    private fun saveDeviceToDatabase(model: DeviceSetupModel) {
        var protocol = ProtocolData()
        if (!url.value.isNullOrBlank()){
            protocol = WifiProtocolData(socketUrl = url.value!!)
        } else if (!hostAddress.value.isNullOrBlank()){
            protocol = BluetoothProtocolData(
                hostId = "",
                hostAddress = hostAddress.value!!
            )
        }
        val deviceData = DeviceData(
            id = UUID.randomUUID().toString(),
            name = model.name,
            imageUrl = model.imagePath ?: "",
            role = model.role,
            offset = DeviceOffsetData(x = model.offsetX, y = model.offsetY, z = model.offsetZ),
            protocol = protocol
        )
        viewModelScope.launch {
            try {
                deviceUseCase.insertDevice(deviceData)
                _saveDeviceResult.value = Result.Success(true)
            } catch (e: Exception){
                _saveDeviceResult.value = Result.Error(e.message ?: "Failed to save")
            }
        }
    }

    fun resetAll() {
        _url.value = ""
        _deviceSetupModel.value = DeviceSetupModel()
        _urlValidationResult.value = null
        _nameValidationResult.value = null
    }

    data class DeviceSetupModel(
        val name: String = "",
        val offsetX: Double = 0.0,
        val offsetY: Double = 0.0,
        val offsetZ: Double = 0.0,
        val role: String = "",
        val imagePath: String? = null,
    )

}