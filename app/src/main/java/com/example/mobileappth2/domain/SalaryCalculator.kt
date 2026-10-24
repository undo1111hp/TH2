package com.example.mobileappth2.domain

import com.example.mobileappth2.data.model.WorkLog
import com.example.mobileappth2.data.model.WorkStatus
import kotlin.math.max
import kotlin.math.round

// Owner: Member C
object SalaryCalculator {
    const val STANDARD_DAILY_HOURS = 8.0
    const val OVERTIME_MULTIPLIER = 1.5

    data class MonthlyResult(
        val totalHours: Double,
        val overtimeHours: Double,
        val totalSalary: Double,
    )

    /**
     * Calculates monthly salary from COMPLETED work logs.
     * Overtime: hours per day above 8 are paid at 1.5x hourlyRate.
     */
    fun calculateMonth(workLogs: List<WorkLog>, hourlyRate: Double): MonthlyResult {
        val completedLogs = workLogs.filter { it.status == WorkStatus.COMPLETED }

        var totalHours = 0.0
        var overtimeHours = 0.0

        for (log in completedLogs) {
            val daily = log.totalHours
            totalHours += daily
            overtimeHours += max(0.0, daily - STANDARD_DAILY_HOURS)
        }

        val regularHours = totalHours - overtimeHours
        val totalSalary = (regularHours * hourlyRate) + (overtimeHours * hourlyRate * OVERTIME_MULTIPLIER)

        return MonthlyResult(
            totalHours = round2(totalHours),
            overtimeHours = round2(overtimeHours),
            totalSalary = round2(totalSalary),
        )
    }

    private fun round2(value: Double): Double = round(value * 100.0) / 100.0
}
