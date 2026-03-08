package com.example.oweather.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private val dateFormatter: DateTimeFormatter = DateTimeFormatter
    .ofLocalizedDate(FormatStyle.MEDIUM)
    .withLocale(Locale("ru"))

private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofLocalizedDateTime(FormatStyle.SHORT)
    .withLocale(Locale("ru"))

fun formatIsoDate(isoDate: String): String {
    return runCatching {
        LocalDate.parse(isoDate).format(dateFormatter)
    }.getOrDefault(isoDate)
}

fun formatTimestamp(timestamp: Long): String {
    return runCatching {
        Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(dateTimeFormatter)
    }.getOrDefault("-")
}
