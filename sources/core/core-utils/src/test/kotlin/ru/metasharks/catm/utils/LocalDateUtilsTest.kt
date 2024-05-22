package ru.metasharks.catm.utils

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ru.metasharks.catm.utils.date.LocalDateUtils

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocalDateUtilsTest {

    @ParameterizedTest
    @MethodSource
    fun test_parseStringToLocalDateTime(input: String, expected: LocalDate) {
        assertEquals(expected, LocalDateUtils.parseToLocalDate(input))
    }

    @ParameterizedTest
    @MethodSource
    fun test_parseISO8601toLocalDateTime(input: String, expected: LocalDateTime) {
        assertEquals(expected, LocalDateUtils.parseISO8601toLocalDateTime(input))
    }

    @Suppress("UNUSED")
    fun test_parseISO8601toLocalDateTime(): List<Arguments> {
        return listOf(
            Arguments.of("2022-01-01T00:00:00", LocalDateTime(2022, 1, 1, 0, 0, 0)),
            Arguments.of("2022-01-01T00:00:00.000000", LocalDateTime(2022, 1, 1, 0, 0, 0)),
            Arguments.of("2022-01-01", LocalDateTime(2022, 1, 1, 0, 0, 0)),
        )
    }

    @Suppress("UNUSED")
    fun test_parseStringToLocalDateTime(): List<Arguments> {
        return listOf(
            Arguments.of("20.05.2002", LocalDate(2002, 5, 20))
        )
    }
}
