/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

@file:Suppress("unused")

package ktask.base.utils

import kotlinx.datetime.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import java.time.ZoneId
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import java.time.Instant as JavaInstant

/** Alias for kotlinx [LocalDate], to avoid ambiguity with Java's LocalDate. */
typealias KLocalDate = LocalDate

/** Alias for kotlinx [LocalTime], to avoid ambiguity with Java's LocalTime. */
typealias KLocalTime = LocalTime

/** Alias for kotlinx [LocalDateTime], to avoid ambiguity with Java's LocalDateTime. */
typealias KLocalDateTime = LocalDateTime

/** Alias for kotlinx [Instant], to avoid ambiguity with Java's Instant. */
typealias KInstant = Instant

/**
 * Singleton providing time-related utility functions.
 */
object DateTimeUtils {

    /**
     * Returns the current date-time in UTC.
     */
    fun currentUTCDateTime(): KLocalDateTime = Clock.System.now().toLocalDateTime(timeZone = timezone())

    /**
     * Returns the current date in UTC.
     */
    fun currentUTCDate(): KLocalDate = Clock.System.todayIn(timeZone = timezone())

    /**
     * Converts a UTC time to the local time zone.
     */
    fun utcToLocal(utc: KLocalDateTime): KLocalDateTime {
        return utc.toInstant(timeZone = TimeZone.UTC).toLocalDateTime(timeZone = timezone())
    }

    /**
     * Converts a Kotlin [LocalDateTime] to a Java [Date].
     */
    fun KLocalDateTime.toJavaDate(): Date {
        this.toInstant(timeZone = timezone()).toJavaInstant().let {
            return Date.from(it)
        }
    }

    /**
     * Converts a Kotlin [Duration] to a Java [Instant].
     */
    fun Duration.toJavaInstant(): JavaInstant {
        return JavaInstant.ofEpochMilli(
            System.currentTimeMillis() + this.toLong(unit = DurationUnit.MILLISECONDS)
        )
    }

    /**
     * Extension function to check if the given LocalDateTime is in the past.
     *
     * @return Boolean true if the LocalDateTime is before the current system time, false otherwise.
     */
    fun KLocalDateTime.isPast(): Boolean {
        val currentDateTime: LocalDateTime = Clock.System.now().toLocalDateTime(timeZone = timezone())
        return this < currentDateTime
    }

    /**
     * Returns the system's default timezone.
     */
    fun timezone(): TimeZone {
        return TimeZone.currentSystemDefault()
    }

    /**
     * Converts a Java [Date] to a Kotlin [LocalDateTime].
     */
    fun javaDateToLocalDateTime(datetime: Date, zoneId: ZoneId = ZoneId.systemDefault()): KLocalDateTime {
        val localDateTime: java.time.LocalDateTime = datetime.toInstant()
            .atZone(zoneId)
            .toLocalDateTime()

        return localDateTime.toKotlinLocalDateTime()
    }

    /**
     * Converts the given [Duration] to a human-readable format.
     */
    fun formatDuration(duration: Duration): String {
        val days: Long = duration.inWholeDays
        val hours: Long = duration.inWholeHours % 24
        val minutes: Long = duration.inWholeMinutes % 60
        val seconds: Long = duration.inWholeSeconds % 60

        return when {
            days > 0 -> "${days}d ${hours}h ${minutes}m ${seconds}s"
            hours > 0 -> "${hours}h ${minutes}m ${seconds}s"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
}
