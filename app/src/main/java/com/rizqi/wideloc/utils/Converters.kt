package com.rizqi.wideloc.utils

import android.bluetooth.BluetoothClass.Device
import android.os.Build
import androidx.room.TypeConverter
import com.rizqi.wideloc.data.local.entity.DeviceProtocol
import com.rizqi.wideloc.data.local.entity.DeviceRole
import com.rizqi.wideloc.data.local.entity.UWBMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.threeten.bp.format.DateTimeFormatter as ThreeTenFormatter
import org.threeten.bp.LocalDateTime as ThreeTenLocalDateTime

class Converters {

    private val javaFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val threeTenFormatter = ThreeTenFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromDeviceProtocol(value: DeviceProtocol): String = value.name

    @TypeConverter
    fun toDeviceProtocol(value: String): DeviceProtocol = DeviceProtocol.valueOf(value)

    @TypeConverter
    fun fromDeviceRole(value: DeviceRole): String = value.name

    @TypeConverter
    fun toDeviceRole(value: String): DeviceRole = DeviceRole.valueOf(value)

    @TypeConverter
    fun fromUWBMode(value: UWBMode): Int = value.mode

    @TypeConverter
    fun toUWBMode(value: Int): UWBMode = when(value){
        0 -> UWBMode.None
        1 -> UWBMode.TDOA
        2 -> UWBMode.TWR
        else -> UWBMode.TWR
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            value?.format(javaFormatter)
        } else {
            value?.let {
                // You’ll need to convert to ThreeTen's LocalDateTime for < API 26
                val threeTen = ThreeTenLocalDateTime.of(
                    it.year, it.monthValue, it.dayOfMonth,
                    it.hour, it.minute, it.second, it.nano
                )
                threeTen.format(threeTenFormatter)
            }
        }
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.parse(it, javaFormatter)
            } else {
                val threeTen = ThreeTenLocalDateTime.parse(it, threeTenFormatter)
                // Convert ThreeTen to java.time.LocalDateTime for compatibility
                LocalDateTime.of(
                    threeTen.year, threeTen.monthValue, threeTen.dayOfMonth,
                    threeTen.hour, threeTen.minute, threeTen.second, threeTen.nano
                )
            }
        }
    }
}



