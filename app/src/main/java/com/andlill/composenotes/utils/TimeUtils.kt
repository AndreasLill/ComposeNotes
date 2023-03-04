package com.andlill.composenotes.utils

import android.content.Context
import com.andlill.composenotes.R
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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

    fun LocalDate.toDateString(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return this.format(formatter)
    }

    fun LocalTime.toTimeString(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return this.format(formatter)
    }

    /*
    Date string using simplified daily language.
    */
    fun LocalDateTime.toSimpleDateString(context: Context): String {
        val current = LocalDateTime.now()
        return when (this.daysBetween(current)) {
            0 -> {
                String.format("%s, %s", context.getString(R.string.date_today), this.toDateString("HH:mm"))
            }
            1 -> {
                String.format("%s, %s", context.getString(R.string.date_tomorrow), this.toDateString("HH:mm"))
            }
            -1 -> {
                String.format("%s, %s", context.getString(R.string.date_yesterday), this.toDateString("HH:mm"))
            }
            else -> {
                if (this.year != current.year) {
                    this.toDateString("d MMM yyyy, HH:mm")
                }
                else {
                    this.toDateString("d MMM, HH:mm")
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