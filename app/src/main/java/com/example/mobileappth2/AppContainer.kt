package com.example.mobileappth2

import android.content.Context
import com.example.mobileappth2.data.db.AppDatabase
import com.example.mobileappth2.data.repo.AccountRepository
import com.example.mobileappth2.data.repo.EmployeeRepository
import com.example.mobileappth2.data.repo.SalaryRepository
import com.example.mobileappth2.data.repo.WorkLogRepository
import com.example.mobileappth2.session.SessionManager

// One instance for the whole app, passed to every screen.
class AppContainer(context: Context) {
    private val db = AppDatabase(context.applicationContext)
    val session = SessionManager(context.applicationContext)
    val accounts = AccountRepository(db)
    val employees = EmployeeRepository(db)
    val workLogs = WorkLogRepository(db)
    val salaries = SalaryRepository(db)
}
