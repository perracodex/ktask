/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.utils

import kotlinx.datetime.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import java.time.Instant as JavaInstant

/**
 * Singleton providing time-related utility functions.
 */
public object DateTimeUtils {

    /**
     * Returns the current date-time in the system's default time zone.
     */
    public fun LocalDateTime.Companion.current(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(timeZone = timezone())
    }

    /**
     * Returns the current date-time in UTC.
     */
    public fun Instant.Companion.current(): Instant = Clock.System.now()

    /**
     * Converts a Kotlin [LocalDateTime] to a Java [Date].
     */
    public fun LocalDateTime.toJavaDate(): Date {
        this.toInstant(timeZone = timezone()).toJavaInstant().let { instant ->
            return Date.from(instant)
        }
    }

    /**
     * Converts a Java [Date] to a Kotlin [LocalDateTime].
     *
     * @param zoneId The java time zone to apply during the conversion. Defaults to the system's default time zone.
     */
    public fun Date.toKotlinLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
        return this.toInstant()
            .atZone(zoneId)
            .toLocalDateTime()
            .toKotlinLocalDateTime()
    }

    /**
     * Converts a Kotlin [Duration] to a Java [Instant].
     */
    public fun Duration.toJavaInstant(): JavaInstant {
        return JavaInstant.ofEpochMilli(
            System.currentTimeMillis() + this.toLong(unit = DurationUnit.MILLISECONDS)
        )
    }

    /**
     * Extension function to check if the given LocalDateTime is in the past.
     *
     * @return Boolean true if the LocalDateTime is before the current system time, false otherwise.
     */
    public fun LocalDateTime.isPast(): Boolean {
        val currentDateTime: LocalDateTime = Clock.System.now().toLocalDateTime(timeZone = timezone())
        return this < currentDateTime
    }

    /**
     * Returns the system's default timezone.
     */
    public fun timezone(): TimeZone {
        return TimeZone.currentSystemDefault()
    }

    /**
     * Returns the current date formatted according to the specified language or locale.
     *
     * @param language The ISO language code, or locale, to format the date.
     * @return The formatted date with the month in title case.
     */
    public fun localizedCurrentDate(language: String): String {
        val locale: Locale = Locale.forLanguageTag(language)
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", locale)
        val currentDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val formattedDate: String = currentDate.toJavaLocalDate().format(dateFormatter)
        return formattedDate.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }
}
