package com.example.todolist.data

import org.junit.Test
import org.junit.Assert.*
import java.util.Date

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun convertsDateToTimestampAndBack_preservesMillis() {
        val millis = 1_695_123_456_789L
        val date = Date(millis)

        val timestamp = converters.dateToTimestamp(date)
        assertNotNull(timestamp)
        assertEquals(millis, timestamp!!)

        val roundTrippedDate = converters.fromTimestamp(timestamp)
        assertNotNull(roundTrippedDate)
        assertEquals(date, roundTrippedDate)
        assertEquals(millis, roundTrippedDate!!.time)
    }

    @Test
    fun convertsPriorityToStringAndBack_preservesEnum() {
        for (priority in Priority.values()) {
            val str = converters.fromPriority(priority)
            val roundTrip = converters.toPriority(str)
            assertEquals(priority, roundTrip)
        }
    }

    @Test
    fun fromPriority_returnsUppercaseEnumName() {
        for (priority in Priority.values()) {
            val result = converters.fromPriority(priority)
            assertEquals(priority.name, result)
            // Ensure it's uppercase
            assertEquals(result, result.uppercase())
        }
    }

    @Test
    fun toPriority_withUnknownString_throwsIllegalArgumentException() {
        try {
            converters.toPriority("UNKNOWN_ENUM_VALUE")
            fail("Expected IllegalArgumentException to be thrown")
        } catch (e: IllegalArgumentException) {
            // expected
        }
    }

    @Test
    fun toPriority_withLowercaseString_throwsIllegalArgumentException() {
        val inputs = listOf("low", "medium", "high", "Low", "High", "MedIum")
        for (input in inputs) {
            try {
                converters.toPriority(input)
                fail("Expected IllegalArgumentException for input: $input")
            } catch (e: IllegalArgumentException) {
                // expected
            }
        }
    }

    @Test
    fun dateConverters_withNullInputs_returnNullOutputs() {
        assertNull(converters.fromTimestamp(null))
        assertNull(converters.dateToTimestamp(null))
    }
}
