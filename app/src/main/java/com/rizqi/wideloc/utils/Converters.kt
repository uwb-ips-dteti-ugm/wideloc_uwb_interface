package com.rizqi.wideloc.utils

import android.os.Build
import androidx.room.TypeConverter
import com.rizqi.wideloc.data.local.entity.DeviceProtocol
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class Converters {

    private val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ISO_LOCAL_DATE_TIME
    } else {
        TODO("VERSION.SDK_INT < O")
    }

    @TypeConverter
    fun fromDeviceProtocol(value: DeviceProtocol): String = value.name

    @TypeConverter
    fun toDeviceProtocol(value: String): DeviceProtocol = DeviceProtocol.valueOf(value)

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            value?.format(formatter)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.parse(it, formatter)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        }
    }
}



