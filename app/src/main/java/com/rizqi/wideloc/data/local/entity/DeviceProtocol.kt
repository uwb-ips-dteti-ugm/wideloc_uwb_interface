package com.rizqi.wideloc.data.local.entity

/**
 * Defines the communication protocol used by a device within the WideLoc system.
 *
 * Devices may rely on different wireless communication technologies for
 * configuration exchange, telemetry reporting, and real-time tracking.
 *
 * @enum WiFi Uses Wi-Fi as the primary communication channel.
 * @enum Bluetooth Uses Bluetooth for device communication.
 */
enum class DeviceProtocol {
    /** Uses Wi-Fi as the device’s communication protocol. */
    WiFi,

    /** Uses Bluetooth as the device’s communication protocol. */
    Bluetooth
}
