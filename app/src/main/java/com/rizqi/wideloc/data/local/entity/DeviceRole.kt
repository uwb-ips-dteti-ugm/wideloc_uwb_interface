package com.rizqi.wideloc.data.local.entity

/**
 * Represents the functional role of a device within the WideLoc UWB tracking system.
 *
 * Each role defines how a device participates in communication, ranging,
 * and overall system coordination.
 *
 * @enum Server Acts as the central coordinator responsible for managing device configurations,
 *        data collection, and tracking sessions.
 * @enum Anchor A fixed UWB node used as a reference point for positioning.
 * @enum Client A mobile device (Tag) that performs ranging with anchors to determine its position.
 */
enum class DeviceRole {

    /** Central coordinator managing system communication and data processing. */
    Server,

    /** Static reference point used for UWB ranging and position calculation. */
    Anchor,

    /** Mobile device (Tag) that performs ranging and reports tracking data. */
    Client
}
