package com.rizqi.wideloc.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDateTime?.formatToString(format: String = "dd MMM yyyy hh:mm"): String {
    return this?.format(
        DateTimeFormatter.ofPattern(format, Locale.ENGLISH)
    ) ?: "Unknown"
}
