package ru.metasharks.catm.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ru.metasharks.catm.utils.strings.getSizeString

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StringUtilsKtTest {

    @ParameterizedTest
    @MethodSource
    fun test_getSizeString(input: Int, expected: String) {
        assertEquals(expected, getSizeString(input))
    }

    @Test
    fun errorTest_getSizeString() {
        val exception =
            assertThrows<IllegalStateException>("Should be error, because file size can't be less or equal to 0") { getSizeString(-1) }
        assertTrue(exception.message != null)
    }

    @Suppress("Unused")
    private fun test_getSizeString(): List<Arguments> {
        return listOf(
            Arguments.of(1, "1,00 b"),
            Arguments.of(1024, "1,00 kb"),
            Arguments.of(1024 * 1024, "1,00 mb"),
            Arguments.of(1024 * 1024 * 1024, "1,00 gb")
        )
    }
}
