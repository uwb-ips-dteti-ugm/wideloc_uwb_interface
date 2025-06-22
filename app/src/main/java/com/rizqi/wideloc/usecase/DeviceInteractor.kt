package com.rizqi.wideloc.usecase

import android.util.Log
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.data.local.entity.DeviceRole
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.URI
import javax.inject.Inject

class DeviceInteractor @Inject constructor(
    private val repository: DeviceRepository
) : DeviceUseCase {

    override fun getAllDevices(): Flow<List<DeviceData>> =
        repository.getAllDevices()

    override fun getAvailableDevices(): Flow<List<DeviceData>> =
        repository.getAllDevices().map { devices ->
            devices.filter { it.isAvailable }
        }

    override fun getReconfigureDevices(): Flow<List<DeviceData>> =
        repository.getAllDevices()
            .map { devices ->
            devices.filter { device ->
                (!device.isAvailable || device.uwbConfigData == null)
            }
        }

    override suspend fun getDeviceById(id: String): DeviceData? =
        repository.getDeviceById(id)

    override suspend fun insertDevice(device: DeviceData) =
        repository.insertDevice(device)

    override suspend fun insertDevices(devices: List<DeviceData>) =
        repository.insertDevices(devices)

    override suspend fun updateDevice(device: DeviceData) =
        repository.updateDevice(device)

    override suspend fun deleteDevice(device: DeviceData) =
        repository.deleteDevice(device)

    override suspend fun deleteDeviceById(id: String) =
        repository.deleteDeviceById(id)

    override suspend fun deleteAllDevices() =
        repository.deleteAllDevices()

    override fun validateSocketUrl(url: String): Result<Boolean> {
        return try {
            // Check if the URL starts with ws:// or wss://
            if (!url.startsWith("ws://") && !url.startsWith("wss://")) {
                return Result.Error("Invalid WebSocket URL: URL must start with ws:// or wss://")
            }

            // Try to create a URI from the URL and check if it's valid
            val uri = URI(url)

            // Check if the URL has a valid host and path structure
            if (uri.host.isNullOrEmpty()) {
                return Result.Error("Invalid WebSocket URL: Missing host")
            }

            // Check if the URL path is valid (optional, but you can define rules if needed)
            if (uri.path.isNullOrEmpty() || uri.path == "/") {
                return Result.Error("Invalid WebSocket URL: Invalid path")
            }

            // URL is valid
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error("Invalid WebSocket URL: ${e.message}")
        }
    }

    override suspend fun getByRole(role: DeviceRole): List<DeviceData> = repository.getByRole(role)

    override suspend fun getFirstByRole(role: DeviceRole): DeviceData? = repository.getFirstByRole(role)

    override suspend fun isAnyServerSaved(): Boolean {
        return getByRole(DeviceRole.Server).isNotEmpty()
    }

    override suspend fun generateNetworkAddress(): Int {
        return (repository.getNetworkAddressLastId() ?: 0) + 1
    }

    override suspend fun generateDeviceAddress(): Int {
        return (repository.getDeviceAddressLastId() ?: 0) + 1
    }
}

