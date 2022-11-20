package com.andlill.composenotes.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

object TimeUtils {

    fun Long.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    }

    fun LocalDateTime.daysBetween(compare: LocalDateTime): Int {
        return ChronoUnit.DAYS.between(
            compare
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0),
            this
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        ).toInt()
    }

    fun LocalDateTime.toDateString(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return this.format(formatter)
    }

    fun Long.toDateString(pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
    }

    fun LocalDateTime.toMilliSeconds(): Long {
        return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}