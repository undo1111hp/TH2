package com.example.mobileappth2.domain

import com.example.mobileappth2.data.model.WorkLog
import com.example.mobileappth2.data.model.WorkStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class SalaryCalculatorTest {

    private fun completedLog(totalHours: Double) = WorkLog(
        id = 0,
        employeeId = 1,
        workDate = "2026-10-01",
        checkInTime = 0L,
        checkOutTime = 0L,
        totalHours = totalHours,
        status = WorkStatus.COMPLETED,
    )

    private fun checkedInLog(totalHours: Double) = WorkLog(
        id = 0,
        employeeId = 1,
        workDate = "2026-10-01",
        checkInTime = 0L,
        checkOutTime = null,
        totalHours = totalHours,
        status = WorkStatus.CHECKED_IN,
    )

    @Test
    fun emptyLogs_returnsZero() {
        val result = SalaryCalculator.calculateMonth(emptyList(), 100.0)
        assertEquals(0.0, result.totalHours, 0.001)
        assertEquals(0.0, result.overtimeHours, 0.001)
        assertEquals(0.0, result.totalSalary, 0.001)
    }

    @Test
    fun onlyCheckedInLogs_ignored() {
        val logs = listOf(checkedInLog(5.0), checkedInLog(10.0))
        val result = SalaryCalculator.calculateMonth(logs, 100.0)
        assertEquals(0.0, result.totalHours, 0.001)
        assertEquals(0.0, result.totalSalary, 0.001)
    }

    @Test
    fun regularHoursOnly_noOvertime() {
        // 3 days x 8h = 24h regular, 0 OT, salary = 24 * 100 = 2400
        val logs = listOf(completedLog(8.0), completedLog(8.0), completedLog(8.0))
        val result = SalaryCalculator.calculateMonth(logs, 100.0)
        assertEquals(24.0, result.totalHours, 0.001)
        assertEquals(0.0, result.overtimeHours, 0.001)
        assertEquals(2400.0, result.totalSalary, 0.001)
    }

    @Test
    fun withOvertime() {
        // Day 1: 10h (8 regular + 2 OT), Day 2: 6h (6 regular)
        // Total: 16h, OT: 2h, Regular: 14h
        // Salary = (14 * 50) + (2 * 50 * 1.5) = 700 + 150 = 850
        val logs = listOf(completedLog(10.0), completedLog(6.0))
        val result = SalaryCalculator.calculateMonth(logs, 50.0)
        assertEquals(16.0, result.totalHours, 0.001)
        assertEquals(2.0, result.overtimeHours, 0.001)
        assertEquals(850.0, result.totalSalary, 0.001)
    }

    @Test
    fun mixedStatuses_onlyCompletedCounted() {
        val logs = listOf(
            completedLog(10.0),
            checkedInLog(5.0),
            completedLog(4.0),
        )
        // Completed: 10h + 4h = 14h total, 2h OT (from the 10h day), 12h regular
        // Salary = (12 * 100) + (2 * 100 * 1.5) = 1200 + 300 = 1500
        val result = SalaryCalculator.calculateMonth(logs, 100.0)
        assertEquals(14.0, result.totalHours, 0.001)
        assertEquals(2.0, result.overtimeHours, 0.001)
        assertEquals(1500.0, result.totalSalary, 0.001)
    }

    @Test
    fun fractionalHours() {
        // 8.5h = 8 regular + 0.5 OT
        // Salary = (8 * 200) + (0.5 * 200 * 1.5) = 1600 + 150 = 1750
        val logs = listOf(completedLog(8.5))
        val result = SalaryCalculator.calculateMonth(logs, 200.0)
        assertEquals(8.5, result.totalHours, 0.001)
        assertEquals(0.5, result.overtimeHours, 0.001)
        assertEquals(1750.0, result.totalSalary, 0.001)
    }

    @Test
    fun zeroHourlyRate_zeroSalary() {
        val logs = listOf(completedLog(10.0))
        val result = SalaryCalculator.calculateMonth(logs, 0.0)
        assertEquals(10.0, result.totalHours, 0.001)
        assertEquals(2.0, result.overtimeHours, 0.001)
        assertEquals(0.0, result.totalSalary, 0.001)
    }
}
