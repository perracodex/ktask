/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.util

import kotlinx.datetime.*
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
        return Clock.System.now().toLocalDateTime(timeZone = TimeZone.current())
    }

    /**
     * Formats a LocalDateTime to a consistent ISO 8601 string with configurable fractional second precision.
     *
     * Fractional seconds are displayed to nanosecond precision (9 digits),
     * which represents the full precision of the LocalDateTime.
     *
     * @param timeDelimiter The delimiter to use between the date and time components. Defaults to "T".
     * @param precision The number of digits for fractional seconds (0 to 9). Defaults to 9 (nanoseconds).
     *                  Values outside this range are coerced to 0 or 9.
     * @return A string representation of the LocalDateTime in ISO 8601 format with the specified fractional precision.
     */
    public fun LocalDateTime.formatted(timeDelimiter: String = "T", precision: Int = 9): String {
        val year: String = "%04d".format(this.year)
        val month: String = "%02d".format(this.monthNumber)
        val day: String = "%02d".format(this.dayOfMonth)
        val hour: String = "%02d".format(this.hour)
        val minute: String = "%02d".format(this.minute)
        val second: String = "%02d".format(this.second)

        // If no fractional seconds are needed, return the basic timestamp.
        val adjustedPrecision: Int = precision.coerceIn(minimumValue = 0, maximumValue = 9)
        if (adjustedPrecision == 0) {
            return "$year-$month-$day$timeDelimiter$hour:$minute:$second"
        }

        // Extract the desired number of digits for fractional seconds.
        val nanosecond: String = "%09d".format(this.nanosecond)
        val fractionalPrecision: String = nanosecond.substring(startIndex = 0, endIndex = adjustedPrecision)

        // Assemble the final formatted string
        return "$year-$month-$day$timeDelimiter$hour:$minute:$second.$fractionalPrecision"
    }

    /**
     * Returns the current date-time in UTC.
     */
    public fun Instant.Companion.current(): Instant = Clock.System.now()

    /**
     * Converts a Kotlin [LocalDateTime] to a Java [Date].
     */
    public fun LocalDateTime.toJavaDate(): Date {
        this.toInstant(timeZone = TimeZone.current()).toJavaInstant().let { instant ->
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
        val currentDateTime: LocalDateTime = Clock.System.now().toLocalDateTime(timeZone = TimeZone.current())
        return this < currentDateTime
    }

    /**
     * Returns the system's default timezone.
     *
     * Equivalent to [TimeZone.currentSystemDefault].
     */
    public fun TimeZone.Companion.current(): TimeZone {
        return currentSystemDefault()
    }

    /**
     * Returns the current date formatted according to the specified language or locale.
     *
     * @param language The ISO language code, or locale, to format the date.
     * @return The formatted date with the month in title case.
     */
    public fun LocalDate.Companion.current(language: String): String {
        val locale: Locale = Locale.forLanguageTag(language)
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", locale)
        val currentDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val formattedDate: String = currentDate.toJavaLocalDate().format(dateFormatter)
        return formattedDate.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }
}
