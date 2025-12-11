package com.rizqi.wideloc.data.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Represents the top–level response structure containing
 * information about multiple UWB clients obtained from the server.
 *
 * This DTO is typically used when fetching the latest state of all
 * connected clients in the UWB network. Each client entry is represented
 * by a [ClientDto].
 *
 * @property clients The list of client information objects returned by the server.
 */
data class ClientInfoDto(
    val clients: List<ClientDto>
)

/**
 * Represents information about a single UWB client in the network.
 *
 * This DTO is used to transmit basic status information about
 * a device participating in Two-Way Ranging (TWR) or related UWB operations.
 *
 * @property address The numeric address or identifier assigned to the client device.
 * @property mode The operational mode of the client (e.g., initiator, responder, passive listener).
 * @property lastUpdate The timestamp (in milliseconds or seconds, depending on API) of the last received update.
 */
data class ClientDto(
    val address: Int,
    val mode: Int,
    @SerializedName("last_update") val lastUpdate: Int
)
