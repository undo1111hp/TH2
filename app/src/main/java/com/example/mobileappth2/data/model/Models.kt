package com.example.mobileappth2.data.model

object Role {
    const val ADMIN = "ADMIN"
    const val EMPLOYEE = "EMPLOYEE"
}

object WorkStatus {
    const val CHECKED_IN = "CHECKED_IN"
    const val COMPLETED = "COMPLETED"
}

object SalaryStatus {
    const val PENDING = "PENDING"
    const val PAID = "PAID"
}

data class Account(
    val id: Long = 0,
    val username: String,
    val password: String,
    val role: String,
)

data class Employee(
    val id: Long = 0,
    val code: String,
    val fullname: String,
    val email: String,
    val phone: String,
    val hourlyRate: Double,
    val accountId: Long,
)

// workDate = "yyyy-MM-dd"; checkInTime/checkOutTime = epoch millis
data class WorkLog(
    val id: Long = 0,
    val employeeId: Long,
    val workDate: String,
    val checkInTime: Long?,
    val checkOutTime: Long?,
    val totalHours: Double = 0.0,
    val status: String = WorkStatus.CHECKED_IN,
)

// totalHours includes overtimeHours
data class Salary(
    val id: Long = 0,
    val employeeId: Long,
    val month: Int,
    val year: Int,
    val totalHours: Double,
    val overtimeHours: Double,
    val totalSalary: Double,
    val status: String = SalaryStatus.PENDING,
)
