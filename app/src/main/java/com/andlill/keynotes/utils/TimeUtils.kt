package com.andlill.keynotes.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*

object TimeUtils {

    fun Long.daysBetween(): Int {
        val current = LocalDateTime
            .now()
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
        val compare = LocalDateTime
            .ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
        return ChronoUnit.DAYS.between(current, compare).toInt()
    }

    fun Long.toDateString(pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
    }

    fun LocalDateTime.toMilliSeconds(): Long {
        return this.toInstant(ZoneOffset.UTC).toEpochMilli()
    }
}