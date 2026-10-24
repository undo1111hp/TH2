package com.example.mobileappth2.domain

import com.example.mobileappth2.data.model.WorkLog
import com.example.mobileappth2.data.model.WorkStatus

// Owner: Member B
data class ValidationResult(
    val isValid: Boolean,
    val message: String = ""
)

object AttendanceValidator {

    /**
     * Checks if employee can check in today.
     * Prevents duplicate check-in on the same day.
     */
    fun validateCheckIn(todayLog: WorkLog?): ValidationResult {
        if (todayLog == null) {
            return ValidationResult(true)
        }
        return when (todayLog.status) {
            WorkStatus.CHECKED_IN -> ValidationResult(false, "You are already checked in. Please check out first.")
            WorkStatus.COMPLETED -> ValidationResult(false, "You have already completed your shift for today.")
            else -> ValidationResult(false, "Invalid attendance status.")
        }
    }

    fun canCheckIn(todayLog: WorkLog?): Boolean = validateCheckIn(todayLog).isValid

    /**
     * Checks if employee can check out.
     * Requires an active CHECKED_IN log and checkOutTime > checkInTime.
     */
    fun validateCheckOut(activeLog: WorkLog?, checkOutTime: Long): ValidationResult {
        if (activeLog == null) {
            return ValidationResult(false, "No active check-in found.")
        }
        if (activeLog.status != WorkStatus.CHECKED_IN) {
            return ValidationResult(false, "You must check in before checking out.")
        }
        val checkIn = activeLog.checkInTime
        if (checkIn == null || checkOutTime <= checkIn) {
            return ValidationResult(false, "Check-out time must be after check-in time.")
        }
        return ValidationResult(true)
    }

    fun canCheckOut(activeLog: WorkLog?, checkOutTime: Long): Boolean =
        validateCheckOut(activeLog, checkOutTime).isValid

    /**
     * Flags incomplete logs: records with date prior to today that remain in CHECKED_IN status.
     */
    fun isIncompleteLog(workLog: WorkLog, todayDate: String): Boolean {
        return workLog.status == WorkStatus.CHECKED_IN && workLog.workDate < todayDate
    }
}
