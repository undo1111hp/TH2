package com.example.mobileappth2.data.repo

import android.content.ContentValues
import android.database.Cursor
import com.example.mobileappth2.data.db.AppDatabase
import com.example.mobileappth2.data.model.Employee
import com.example.mobileappth2.data.model.Salary
import com.example.mobileappth2.data.model.SalaryStatus

// Owner: Member C
class SalaryRepository(private val db: AppDatabase) {

    fun getByEmployeeAndMonth(employeeId: Long, month: Int, year: Int): Salary? {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM Salary WHERE employeeId = ? AND month = ? AND year = ?",
            arrayOf(employeeId.toString(), month.toString(), year.toString())
        )
        return cursor.use { if (it.moveToFirst()) fromCursor(it) else null }
    }

    fun listByMonth(month: Int, year: Int): List<Salary> {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM Salary WHERE month = ? AND year = ? ORDER BY employeeId ASC",
            arrayOf(month.toString(), year.toString())
        )
        return cursor.use { readList(it) }
    }

    fun listByEmployee(employeeId: Long): List<Salary> {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM Salary WHERE employeeId = ? ORDER BY year DESC, month DESC",
            arrayOf(employeeId.toString())
        )
        return cursor.use { readList(it) }
    }

    /** Insert or update salary for the unique (employeeId, month, year) combo. */
    fun upsert(salary: Salary): Long {
        val existing = getByEmployeeAndMonth(salary.employeeId, salary.month, salary.year)
        return if (existing != null) {
            update(salary.copy(id = existing.id))
            existing.id
        } else {
            insert(salary)
        }
    }

    fun insert(salary: Salary): Long {
        val values = toContentValues(salary)
        return db.writableDatabase.insert("Salary", null, values)
    }

    fun update(salary: Salary): Int {
        val values = toContentValues(salary)
        return db.writableDatabase.update("Salary", values, "id = ?", arrayOf(salary.id.toString()))
    }

    fun updateStatus(id: Long, status: String): Int {
        val values = ContentValues().apply { put("status", status) }
        return db.writableDatabase.update("Salary", values, "id = ?", arrayOf(id.toString()))
    }

    fun delete(id: Long): Int {
        return db.writableDatabase.delete("Salary", "id = ?", arrayOf(id.toString()))
    }

    /** Get all employees — used by salary management to iterate. */
    fun listAllEmployees(): List<Employee> {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM Employee ORDER BY code ASC", null
        )
        return cursor.use {
            val list = mutableListOf<Employee>()
            while (it.moveToNext()) list.add(employeeFromCursor(it))
            list
        }
    }

    /** Get employee linked to an account — used by MySalaryScreen. */
    fun getEmployeeByAccountId(accountId: Long): Employee? {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM Employee WHERE accountId = ? LIMIT 1",
            arrayOf(accountId.toString())
        )
        return cursor.use { if (it.moveToFirst()) employeeFromCursor(it) else null }
    }

    /** Get employee by id. */
    fun getEmployeeById(employeeId: Long): Employee? {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM Employee WHERE id = ? LIMIT 1",
            arrayOf(employeeId.toString())
        )
        return cursor.use { if (it.moveToFirst()) employeeFromCursor(it) else null }
    }

    private fun toContentValues(salary: Salary) = ContentValues().apply {
        put("employeeId", salary.employeeId)
        put("month", salary.month)
        put("year", salary.year)
        put("totalHours", salary.totalHours)
        put("overtimeHours", salary.overtimeHours)
        put("totalSalary", salary.totalSalary)
        put("status", salary.status)
    }

    private fun readList(cursor: Cursor): List<Salary> {
        val list = mutableListOf<Salary>()
        while (cursor.moveToNext()) list.add(fromCursor(cursor))
        return list
    }

    private fun fromCursor(cursor: Cursor) = Salary(
        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
        employeeId = cursor.getLong(cursor.getColumnIndexOrThrow("employeeId")),
        month = cursor.getInt(cursor.getColumnIndexOrThrow("month")),
        year = cursor.getInt(cursor.getColumnIndexOrThrow("year")),
        totalHours = cursor.getDouble(cursor.getColumnIndexOrThrow("totalHours")),
        overtimeHours = cursor.getDouble(cursor.getColumnIndexOrThrow("overtimeHours")),
        totalSalary = cursor.getDouble(cursor.getColumnIndexOrThrow("totalSalary")),
        status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
    )

    private fun employeeFromCursor(cursor: Cursor) = Employee(
        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
        code = cursor.getString(cursor.getColumnIndexOrThrow("code")),
        fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname")),
        email = cursor.getString(cursor.getColumnIndexOrThrow("email") ),
        phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
        hourlyRate = cursor.getDouble(cursor.getColumnIndexOrThrow("hourlyRate")),
        accountId = cursor.getLong(cursor.getColumnIndexOrThrow("accountId")),
    )
}
