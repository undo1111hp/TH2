package com.example.mobileappth2.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mobileappth2.data.model.Role
import com.example.mobileappth2.data.model.SalaryStatus
import com.example.mobileappth2.data.model.WorkStatus
import java.util.Calendar
import kotlin.math.round

class AppDatabase(context: Context) : SQLiteOpenHelper(context, "workhours.db", null, 2) {

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE Account(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL)"""
        )
        db.execSQL(
            """CREATE TABLE Employee(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                code TEXT NOT NULL UNIQUE,
                fullname TEXT NOT NULL,
                email TEXT,
                phone TEXT,
                hourlyRate REAL NOT NULL,
                accountId INTEGER NOT NULL REFERENCES Account(id) ON DELETE CASCADE)"""
        )
        db.execSQL(
            """CREATE TABLE WorkLog(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employeeId INTEGER NOT NULL REFERENCES Employee(id) ON DELETE CASCADE,
                workDate TEXT NOT NULL,
                checkInTime INTEGER,
                checkOutTime INTEGER,
                totalHours REAL NOT NULL DEFAULT 0,
                status TEXT NOT NULL)"""
        )
        db.execSQL(
            """CREATE TABLE Salary(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employeeId INTEGER NOT NULL REFERENCES Employee(id) ON DELETE CASCADE,
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                totalHours REAL NOT NULL,
                overtimeHours REAL NOT NULL,
                totalSalary REAL NOT NULL,
                status TEXT NOT NULL,
                UNIQUE(employeeId, month, year))"""
        )
        seed(db)
    }

    // Dev phase: drop and recreate. Uninstall the app if the schema changes.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        listOf("Salary", "WorkLog", "Employee", "Account").forEach { db.execSQL("DROP TABLE IF EXISTS $it") }
        onCreate(db)
    }

    private fun seed(db: SQLiteDatabase) {
        // Accounts: admin + two employees
        db.insert("Account", null, ContentValues().apply {
            put("username", "admin"); put("password", "admin123"); put("role", Role.ADMIN)
        })
        val a1 = db.insert("Account", null, ContentValues().apply {
            put("username", "emp01"); put("password", "123456"); put("role", Role.EMPLOYEE)
        })
        val a2 = db.insert("Account", null, ContentValues().apply {
            put("username", "emp02"); put("password", "123456"); put("role", Role.EMPLOYEE)
        })

        // Employees
        val e1 = db.insert("Employee", null, ContentValues().apply {
            put("code", "EMP01"); put("fullname", "Nguyen Van A"); put("email", "a@example.com")
            put("phone", "0900000001"); put("hourlyRate", 50000.0); put("accountId", a1)
        })
        val e2 = db.insert("Employee", null, ContentValues().apply {
            put("code", "EMP02"); put("fullname", "Tran Thi B"); put("email", "b@example.com")
            put("phone", "0900000002"); put("hourlyRate", 60000.0); put("accountId", a2)
        })

        // --- WorkLogs: EMP01 September 2026 (complete month) ---
        seedLog(db, e1, "2026-09-01", 8, 0, 17, 0)   // 9h  (1h OT)
        seedLog(db, e1, "2026-09-02", 8, 0, 16, 0)   // 8h
        seedLog(db, e1, "2026-09-03", 8, 0, 18, 0)   // 10h (2h OT)
        seedLog(db, e1, "2026-09-04", 8, 0, 16, 30)  // 8.5h (0.5h OT)
        seedLog(db, e1, "2026-09-05", 8, 0, 16, 0)   // 8h

        // --- WorkLogs: EMP01 October 2026 (current month, partial) ---
        seedLog(db, e1, "2026-10-01", 8, 0, 17, 0)   // 9h
        seedLog(db, e1, "2026-10-02", 8, 0, 16, 0)   // 8h
        seedLog(db, e1, "2026-10-03", 8, 0, 18, 30)  // 10.5h

        // --- WorkLogs: EMP02 September 2026 ---
        seedLog(db, e2, "2026-09-01", 9, 0, 17, 0)   // 8h
        seedLog(db, e2, "2026-09-02", 9, 0, 18, 0)   // 9h  (1h OT)
        seedLog(db, e2, "2026-09-03", 9, 0, 17, 0)   // 8h
        seedLog(db, e2, "2026-09-04", 9, 0, 19, 0)   // 10h (2h OT)

        // --- WorkLogs: EMP02 October 2026 (partial) ---
        seedLog(db, e2, "2026-10-01", 9, 0, 17, 0)   // 8h
        seedLog(db, e2, "2026-10-02", 9, 0, 18, 0)   // 9h

        // --- Salary: EMP01 Sep 2026 — 43.5h, 3.5h OT, PAID ---
        // Regular: 40h * 50000 + OT: 3.5h * 50000 * 1.5 = 2,262,500
        db.insert("Salary", null, ContentValues().apply {
            put("employeeId", e1); put("month", 9); put("year", 2026)
            put("totalHours", 43.5); put("overtimeHours", 3.5)
            put("totalSalary", 2262500.0); put("status", SalaryStatus.PAID)
        })

        // --- Salary: EMP02 Sep 2026 — 35h, 3h OT, PENDING ---
        // Regular: 32h * 60000 + OT: 3h * 60000 * 1.5 = 2,190,000
        db.insert("Salary", null, ContentValues().apply {
            put("employeeId", e2); put("month", 9); put("year", 2026)
            put("totalHours", 35.0); put("overtimeHours", 3.0)
            put("totalSalary", 2190000.0); put("status", SalaryStatus.PENDING)
        })
    }

    private fun seedLog(db: SQLiteDatabase, empId: Long, date: String, inH: Int, inM: Int, outH: Int, outM: Int) {
        val parts = date.split("-").map { it.toInt() }
        val cal = Calendar.getInstance()
        cal.set(parts[0], parts[1] - 1, parts[2], inH, inM, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val cin = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, outH); cal.set(Calendar.MINUTE, outM)
        val cout = cal.timeInMillis
        val hours = round((cout - cin).toDouble() / 3_600_000.0 * 100) / 100.0
        db.insert("WorkLog", null, ContentValues().apply {
            put("employeeId", empId); put("workDate", date)
            put("checkInTime", cin); put("checkOutTime", cout)
            put("totalHours", hours); put("status", WorkStatus.COMPLETED)
        })
    }
}
