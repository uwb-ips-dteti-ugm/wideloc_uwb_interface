package com.rizqi.wideloc.utils

import java.util.Locale

private const val ipv4Pattern = "[0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}"
private const val portPattern = ":\\d*"

/**
 * Replace host address with another ip address.
 *
 * Example: 10.72.61.31:1246 replaced to 10.72.60.30:1080
 */
fun CharSequence.replaceHostAddress(anotherIPAddress: String, anotherPort: String): String {
    val newUrl = "${anotherIPAddress}:${anotherPort}"
    val ipRegex = "$ipv4Pattern$portPattern".toRegex()
    return this.toString().replace(ipRegex, newUrl)
}

fun CharSequence.isValidHostAddress(): Boolean {
    val regex = "wss?:\\/\\/$ipv4Pattern(:(\\d*))?\\/".toRegex()
    return regex.matches(this)
}

fun Double.toDisplayString(decimalNumber: Int = 5): String = String.format("%.${decimalNumber}f", this).trimEnd('0').trimEnd('.')

fun Long.toTimerString(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds)
}
