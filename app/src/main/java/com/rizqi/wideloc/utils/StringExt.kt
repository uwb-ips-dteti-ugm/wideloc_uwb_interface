package com.rizqi.wideloc.utils

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

fun Double.toDisplayString(): String = String.format("%.5f", this).trimEnd('0').trimEnd('.')
