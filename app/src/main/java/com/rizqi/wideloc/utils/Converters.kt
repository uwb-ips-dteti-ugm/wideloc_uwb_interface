package com.rizqi.wideloc.utils

import androidx.room.TypeConverter
import com.rizqi.wideloc.data.local.entity.DeviceProtocol

class Converters {
    @TypeConverter
    fun fromDeviceProtocol(value: DeviceProtocol): String = value.name

    @TypeConverter
    fun toDeviceProtocol(value: String): DeviceProtocol = DeviceProtocol.valueOf(value)
}
