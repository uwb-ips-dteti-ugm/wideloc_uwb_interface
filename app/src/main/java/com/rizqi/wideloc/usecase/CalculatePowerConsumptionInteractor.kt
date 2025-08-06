package com.rizqi.wideloc.usecase

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.rizqi.wideloc.domain.model.PowerConsumptionData
import kotlin.math.abs

class CalculatePowerConsumptionInteractor : CalculatePowerConsumptionUseCase {
    override fun invoke(
        context: Context,
        startTime: Long,
        endTime: Long,
        startBatteryLevel: Int,
        endBatteryLevel: Int
    ): PowerConsumptionData {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val currentMicroAmps = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)

        val batteryStatus = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val rawVoltageMilliVolts = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
        val voltageVolts = if (rawVoltageMilliVolts > 0) rawVoltageMilliVolts / 1000.0 else 3.7

        val powerMilliWatts = if (currentMicroAmps != Long.MIN_VALUE) {
            val currentMilliAmps = abs(currentMicroAmps) / 1000.0 // discharge → positive
            currentMilliAmps * voltageVolts
        } else {
            0.0
        }

        val batteryDrop = endBatteryLevel - startBatteryLevel
        val durationInMilliSeconds = (endTime - startTime).toDouble() / 1_000_000

        return PowerConsumptionData(
            powerMilliWatts = powerMilliWatts,
            currentMicroAmps = currentMicroAmps,
            startBatteryLevel = startBatteryLevel,
            endBatteryLevel = endBatteryLevel,
            batteryDrop = batteryDrop,
            durationInMilliSeconds = durationInMilliSeconds,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun getBatteryLevel(context: Context): Int {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    }
}

