package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.wideloc.usecase.DeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.rizqi.wideloc.data.Result

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val deviceUseCase: DeviceUseCase
) : ViewModel() {

    private val _url = MutableLiveData<String?>()
    val url: LiveData<String?> get() = _url

    private val _urlValidationResult = MutableLiveData<Result<Boolean>?>()
    val urlValidationResult: LiveData<Result<Boolean>?> get() = _urlValidationResult

    private val _hostAddress = MutableLiveData<String?>()
    val hostAddress: LiveData<String?> get() = _hostAddress

    fun setUrl(newUrl: String){
        deviceUseCase.validateSocketUrl(newUrl).also {
            if (it is Result.Success){
                _url.value = newUrl
            }
            _urlValidationResult.value = it
        }
    }

    fun resetAll() {
        _url.value = ""
        _urlValidationResult.value = null
    }

}