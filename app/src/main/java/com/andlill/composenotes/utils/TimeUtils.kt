package com.andlill.composenotes.utils

import android.content.Context
import android.text.format.DateFormat
import com.andlill.composenotes.R
import java.time.*
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
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        return this.format(formatter)
    }

    fun LocalDate.toDateString(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        return this.format(formatter)
    }

    fun LocalTime.toSimpleTimeString(context: Context): String {
        val is24h = DateFormat.is24HourFormat(context)
        val pattern = if (is24h) "HH:mm" else "h:mm a"
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        return this.format(formatter)
    }

    fun LocalDateTime.toSimpleTimeString(context: Context): String {
        val is24h = DateFormat.is24HourFormat(context)
        val pattern = if (is24h) "HH:mm" else "h:mm a"
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        return this.format(formatter)
    }

    /*
    Date string using simplified daily language.
    */
    fun LocalDateTime.toSimpleDateString(context: Context): String {
        val current = LocalDateTime.now()
        return when (this.daysBetween(current)) {
            0 -> {
                String.format("%s, %s", context.getString(R.string.date_today), this.toSimpleTimeString(context))
            }
            1 -> {
                String.format("%s, %s", context.getString(R.string.date_tomorrow), this.toSimpleTimeString(context))
            }
            -1 -> {
                String.format("%s, %s", context.getString(R.string.date_yesterday), this.toSimpleTimeString(context))
            }
            else -> {
                if (this.year != current.year) {
                    String.format("%s, %s", this.toDateString("d MMM yyyy"), this.toSimpleTimeString(context))
                }
                else {
                    String.format("%s, %s", this.toDateString("d MMM"), this.toSimpleTimeString(context))
                }
            }
        }
    }

    fun LocalDateTime.toMilliSeconds(): Long {
        return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    // Returns a LocalDateTime with the closest day of the month available.
    fun LocalDateTime.withClosestDayOfMonth(dayOfMonth: Int): LocalDateTime {
        val isLeapYear = this.toLocalDate().isLeapYear
        val monthLength = this.month.length(isLeapYear)

        return if (dayOfMonth > monthLength)
            this.withDayOfMonth(monthLength)
        else
            this.withDayOfMonth(dayOfMonth)
    }
}