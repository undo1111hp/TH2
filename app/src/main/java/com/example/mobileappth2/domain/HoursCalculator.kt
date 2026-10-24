package com.example.mobileappth2.domain

import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

// Owner: Member B
object HoursCalculator {
    const val STANDARD_DAILY_HOURS = 8.0

    /**
     * Calculates total hours between checkInTime and checkOutTime (epoch millis).
     * Returns double rounded to 2 decimal places. Returns 0.0 if invalid.
     */
    fun calculateTotalHours(checkInTime: Long?, checkOutTime: Long?): Double {
        if (checkInTime == null || checkOutTime == null || checkOutTime <= checkInTime) {
            return 0.0
        }
        val diffMillis = checkOutTime - checkInTime
        val hours = diffMillis.toDouble() / (1000.0 * 60 * 60)
        return round(hours * 100.0) / 100.0
    }

    /**
     * Hours worked beyond standard daily hours (8.0h).
     */
    fun calculateOvertimeHours(totalHours: Double): Double {
        val overtime = max(0.0, totalHours - STANDARD_DAILY_HOURS)
        return round(overtime * 100.0) / 100.0
    }

    /**
     * Regular hours capped at standard daily hours.
     */
    fun calculateRegularHours(totalHours: Double): Double {
        val regular = min(totalHours, STANDARD_DAILY_HOURS)
        return round(regular * 100.0) / 100.0
    }
}
