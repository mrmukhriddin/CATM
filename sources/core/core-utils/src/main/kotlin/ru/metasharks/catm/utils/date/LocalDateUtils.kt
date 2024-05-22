package ru.metasharks.catm.utils.date

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

object LocalDateUtils {

    const val DAY_OF_YEAR_PATTERN = "dd.MM.yyyy"
    const val TIME_OF_DAY_PATTERN = "HH:mm"
    const val DATE_TIME_OF_DAY_PATTERN = "dd.MM.yyyy HH:mm"

    val ISOFormats = listOf(
        ISODateTimeFormat.localDateOptionalTimeParser(),
        ISODateTimeFormat.date(),
        ISODateTimeFormat.dateTimeNoMillis(),
        ISODateTimeFormat.dateTime(),
    )

    private const val DEFAULT_TIME_VALUE = "00:00"

    var formatter: DateTimeFormatter? = null
        get() {
            if (field == null) {
                field = DateTimeFormat.forPattern(DAY_OF_YEAR_PATTERN)
            }
            return field
        }

    var timeFormatter: DateTimeFormatter? = null
        get() {
            if (field == null) {
                field = DateTimeFormat.forPattern(TIME_OF_DAY_PATTERN)
            }
            return field
        }

    var dateTimeFormatter: DateTimeFormatter? = null
        get() {
            if (field == null) {
                field = DateTimeFormat.forPattern(DATE_TIME_OF_DAY_PATTERN)
            }
            return field
        }

    fun getISO8601DateString(dateString: String): String {
        val splitDate = dateString.split('.').map { it.toInt() }
        val dayOfMonth = splitDate[0]
        val monthOfYear = splitDate[1]
        val year = splitDate[2]
        return String.format(
            "%04d-%02d-%02d",
            year,
            monthOfYear,
            dayOfMonth,
        )
    }

    fun squashDateAndTimeToISO8601(
        dateString: String,
        timeString: String = DEFAULT_TIME_VALUE
    ): String {
        val splitDate = dateString.split('.').map { it.toInt() }
        val dayOfMonth = splitDate[0]
        val monthOfYear = splitDate[1]
        val year = splitDate[2]
        val splitTime = timeString.split(':').map { it.toInt() }
        val hour = splitTime[0]
        val minutes = splitTime[1]
        return String.format(
            "%04d-%02d-%02dT%02d:%02d:00",
            year,
            monthOfYear,
            dayOfMonth,
            hour,
            minutes
        )
    }

    @Suppress("TooGenericExceptionCaught")
    fun checkIfValid(stringISO8601: String): Boolean {
        return try {
            parseISO8601toLocalDateTime(stringISO8601)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun parseISO8601toLocalDateTime(stringISO8601: String): LocalDateTime {
        var result: LocalDateTime?
        for (format in ISOFormats) {
            result = try {
                LocalDateTime.parse(stringISO8601, format)
            } catch (ex: IllegalArgumentException) {
                null
            }
            if (result != null) {
                return result
            }
        }
        throw IllegalArgumentException(
            "Unknown type of string $stringISO8601, not compatible with any format ${
                ISOFormats.joinToString(separator = ",", transform = {
                    it.toString()
                })
            }"
        )
    }

    fun toISO8601String(localDateTime: LocalDateTime): String {
        return ISODateTimeFormat.dateTime().print(localDateTime)
    }

    /**
     * Преобразовывает строку формата "dd.MM.yyyy" в LocalDate
     */
    fun parseToLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, formatter)
    }

    /**
     * Преобразовывает строку формата "HH:mm" в LocalTime
     */
    fun parseToLocalTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString, timeFormatter)
    }

    /**
     * Преобразовывает LocalDate в строку формата "dd.MM.yyyy"
     */
    fun toString(date: LocalDate): String {
        return date.toString(formatter)
    }

    /**
     * Преобразовывает LocalDate и LocalTime в строку формата "dd.MM.yyyy HH:mm"
     */
    fun toString(date: LocalDate, time: LocalTime): String {
        return StringBuilder()
            .append(toString(date))
            .append(' ')
            .append(toString(time))
            .toString()
    }

    /**
     * Преобразовывает LocalTime в строку формата "HH:mm"
     */
    fun toString(time: LocalTime): String {
        return time.toString(timeFormatter)
    }

    /**
     * Преобразовывает LocalDateTime в строку формата "dd.MM.yyyy HH:mm"
     */
    fun toString(time: LocalDateTime): String {
        return time.toString(dateTimeFormatter)
    }

    fun fromMillis(lastTimeSaved: Long): LocalDateTime {
        return DateTime(lastTimeSaved).toLocalDateTime()
    }

    fun parseLocalDateTime(dateTimeString: String): LocalDateTime {
        return try {
             parseISO8601toLocalDateTime(dateTimeString)
        } catch (ex: IllegalArgumentException) {
            LocalDateTime.parse(dateTimeString, dateTimeFormatter)
        }
    }
}
