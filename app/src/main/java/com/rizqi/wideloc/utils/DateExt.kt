package com.rizqi.wideloc.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun LocalDateTime?.formatToString(format: String = DateTimeFormat.ddMMMyyyyhhmm): String {
    return this?.format(
        DateTimeFormatter.ofPattern(format, Locale.ENGLISH)
    ) ?: "Unknown"
}

fun formatTimestamp(timestamp: Long, format: String = DateTimeFormat.EEEEddMMMMyyyy): String {
    val date = Date(timestamp)
    val formater = SimpleDateFormat(format, Locale.ENGLISH)
    return formater.format(date)
}

object DateTimeFormat {
    const val ddMMMyyyyhhmm = "dd MMM yyyy hh:mm"
    const val EEEEddMMMMyyyy = "EEEE, dd MMMM yyyy"
    const val hhmmss = "hh:mm:ss"
}

