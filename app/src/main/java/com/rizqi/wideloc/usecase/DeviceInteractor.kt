package com.rizqi.wideloc.usecase

import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.data.local.entity.DeviceEntity
import com.rizqi.wideloc.domain.DeviceRepository
import kotlinx.coroutines.flow.Flow
import java.net.URI
import javax.inject.Inject

class DeviceInteractor @Inject constructor(
    private val repository: DeviceRepository
) : DeviceUseCase {

    override fun getAllDevices(): Flow<List<DeviceEntity>> =
        repository.getAllDevices()

    override suspend fun getDeviceById(id: String): DeviceEntity? =
        repository.getDeviceById(id)

    override suspend fun insertDevice(device: DeviceEntity) =
        repository.insertDevice(device)

    override suspend fun insertDevices(devices: List<DeviceEntity>) =
        repository.insertDevices(devices)

    override suspend fun updateDevice(device: DeviceEntity) =
        repository.updateDevice(device)

    override suspend fun deleteDevice(device: DeviceEntity) =
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
}

