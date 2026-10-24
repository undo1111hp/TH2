package com.example.mobileappth2.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class HoursCalculatorTest {

    @Test
    fun calculateTotalHours_standard8Hours_returns8() {
        val checkIn = 1700000000000L
        val checkOut = checkIn + TimeUnit.HOURS.toMillis(8)
        val result = HoursCalculator.calculateTotalHours(checkIn, checkOut)
        assertEquals(8.0, result, 0.001)
    }

    @Test
    fun calculateTotalHours_fractionalHours_roundsToTwoDecimals() {
        val checkIn = 1700000000000L
        val checkOut = checkIn + TimeUnit.HOURS.toMillis(4) + TimeUnit.MINUTES.toMillis(30)
        val result = HoursCalculator.calculateTotalHours(checkIn, checkOut)
        assertEquals(4.5, result, 0.001)
    }

    @Test
    fun calculateTotalHours_invalidTimes_returnsZero() {
        assertEquals(0.0, HoursCalculator.calculateTotalHours(null, 1000L), 0.001)
        assertEquals(0.0, HoursCalculator.calculateTotalHours(1000L, null), 0.001)
        assertEquals(0.0, HoursCalculator.calculateTotalHours(2000L, 1000L), 0.001)
        assertEquals(0.0, HoursCalculator.calculateTotalHours(1000L, 1000L), 0.001)
    }

    @Test
    fun calculateOvertimeHours_above8Hours_returnsExcess() {
        assertEquals(2.0, HoursCalculator.calculateOvertimeHours(10.0), 0.001)
        assertEquals(1.5, HoursCalculator.calculateOvertimeHours(9.5), 0.001)
    }

    @Test
    fun calculateOvertimeHours_underOrEqual8Hours_returnsZero() {
        assertEquals(0.0, HoursCalculator.calculateOvertimeHours(8.0), 0.001)
        assertEquals(0.0, HoursCalculator.calculateOvertimeHours(6.5), 0.001)
    }

    @Test
    fun calculateRegularHours_capsAt8Hours() {
        assertEquals(8.0, HoursCalculator.calculateRegularHours(10.0), 0.001)
        assertEquals(7.5, HoursCalculator.calculateRegularHours(7.5), 0.001)
    }
}
