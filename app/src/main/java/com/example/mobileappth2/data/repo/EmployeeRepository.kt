package com.example.mobileappth2.data.repo

import android.content.ContentValues
import android.database.Cursor
import com.example.mobileappth2.data.db.AppDatabase
import com.example.mobileappth2.data.model.Employee
import com.example.mobileappth2.data.model.Role

// Owner: Member A
class EmployeeRepository(private val db: AppDatabase) {

    // Matches code or fullname; blank query returns everyone
    fun search(query: String = ""): List<Employee> {
        val like = "%${query.trim()}%"
        return db.readableDatabase.rawQuery(
            "SELECT * FROM Employee WHERE code LIKE ? OR fullname LIKE ? ORDER BY fullname",
            arrayOf(like, like),
        ).use { c -> buildList { while (c.moveToNext()) add(c.toEmployee()) } }
    }

    fun getById(id: Long): Employee? =
        db.readableDatabase.rawQuery("SELECT * FROM Employee WHERE id = ?", arrayOf(id.toString()))
            .use { if (it.moveToFirst()) it.toEmployee() else null }

    // Creates the login Account and the Employee together. Returns the new id, or -1 if the code/username is taken.
    fun insert(employee: Employee, username: String, password: String): Long {
        val conn = db.writableDatabase
        conn.beginTransaction()
        try {
            val accountId = conn.insert("Account", null, ContentValues().apply {
                put("username", username); put("password", password); put("role", Role.EMPLOYEE)
            })
            if (accountId == -1L) return -1
            val id = conn.insert("Employee", null, employee.copy(accountId = accountId).toValues())
            if (id == -1L) return -1
            conn.setTransactionSuccessful()
            return id
        } finally {
            conn.endTransaction()
        }
    }

    // False if nothing was updated (e.g. the new code is already used)
    fun update(employee: Employee): Boolean = try {
        db.writableDatabase.update("Employee", employee.toValues(), "id = ?", arrayOf(employee.id.toString())) > 0
    } catch (e: android.database.sqlite.SQLiteConstraintException) {
        false
    }

    // Deleting the Account cascades to Employee, WorkLog and Salary
    fun delete(id: Long): Boolean {
        val employee = getById(id) ?: return false
        return db.writableDatabase.delete("Account", "id = ?", arrayOf(employee.accountId.toString())) > 0
    }

    private fun Employee.toValues() = ContentValues().apply {
        put("code", code); put("fullname", fullname); put("email", email)
        put("phone", phone); put("hourlyRate", hourlyRate); put("accountId", accountId)
    }

    private fun Cursor.toEmployee() = Employee(
        id = getLong(getColumnIndexOrThrow("id")),
        code = getString(getColumnIndexOrThrow("code")),
        fullname = getString(getColumnIndexOrThrow("fullname")),
        email = getString(getColumnIndexOrThrow("email")) ?: "",
        phone = getString(getColumnIndexOrThrow("phone")) ?: "",
        hourlyRate = getDouble(getColumnIndexOrThrow("hourlyRate")),
        accountId = getLong(getColumnIndexOrThrow("accountId")),
    )
}
