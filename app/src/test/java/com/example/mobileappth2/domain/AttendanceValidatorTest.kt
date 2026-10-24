package com.example.mobileappth2.domain

import com.example.mobileappth2.data.model.WorkLog
import com.example.mobileappth2.data.model.WorkStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AttendanceValidatorTest {

    @Test
    fun canCheckIn_whenNoLogToday_returnsTrue() {
        assertTrue(AttendanceValidator.canCheckIn(null))
    }

    @Test
    fun canCheckIn_whenAlreadyCheckedIn_returnsFalse() {
        val activeLog = WorkLog(
            id = 1,
            employeeId = 1,
            workDate = "2026-10-24",
            checkInTime = System.currentTimeMillis(),
            checkOutTime = null,
            status = WorkStatus.CHECKED_IN
        )
        assertFalse(AttendanceValidator.canCheckIn(activeLog))
    }

    @Test
    fun canCheckIn_whenCompletedShiftToday_returnsFalse() {
        val completedLog = WorkLog(
            id = 1,
            employeeId = 1,
            workDate = "2026-10-24",
            checkInTime = 1000L,
            checkOutTime = 2000L,
            totalHours = 8.0,
            status = WorkStatus.COMPLETED
        )
        assertFalse(AttendanceValidator.canCheckIn(completedLog))
    }

    @Test
    fun canCheckOut_withValidActiveLog_returnsTrue() {
        val activeLog = WorkLog(
            id = 1,
            employeeId = 1,
            workDate = "2026-10-24",
            checkInTime = 1000L,
            checkOutTime = null,
            status = WorkStatus.CHECKED_IN
        )
        assertTrue(AttendanceValidator.canCheckOut(activeLog, 2000L))
    }

    @Test
    fun canCheckOut_whenCheckOutBeforeCheckIn_returnsFalse() {
        val activeLog = WorkLog(
            id = 1,
            employeeId = 1,
            workDate = "2026-10-24",
            checkInTime = 2000L,
            checkOutTime = null,
            status = WorkStatus.CHECKED_IN
        )
        assertFalse(AttendanceValidator.canCheckOut(activeLog, 1000L))
        assertFalse(AttendanceValidator.canCheckOut(activeLog, 2000L))
    }

    @Test
    fun canCheckOut_whenAlreadyCompleted_returnsFalse() {
        val completedLog = WorkLog(
            id = 1,
            employeeId = 1,
            workDate = "2026-10-24",
            checkInTime = 1000L,
            checkOutTime = 2000L,
            status = WorkStatus.COMPLETED
        )
        assertFalse(AttendanceValidator.canCheckOut(completedLog, 3000L))
    }

    @Test
    fun isIncompleteLog_priorDateCheckedIn_returnsTrue() {
        val oldLog = WorkLog(
            id = 1,
            employeeId = 1,
            workDate = "2026-10-23",
            checkInTime = 1000L,
            checkOutTime = null,
            status = WorkStatus.CHECKED_IN
        )
        assertTrue(AttendanceValidator.isIncompleteLog(oldLog, "2026-10-24"))
    }

    @Test
    fun isIncompleteLog_todayCheckedIn_returnsFalse() {
        val todayLog = WorkLog(
            id = 1,
            employeeId = 1,
            workDate = "2026-10-24",
            checkInTime = 1000L,
            checkOutTime = null,
            status = WorkStatus.CHECKED_IN
        )
        assertFalse(AttendanceValidator.isIncompleteLog(todayLog, "2026-10-24"))
    }
}
