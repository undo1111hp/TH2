package com.example.mobileappth2.data.repo

import android.content.ContentValues
import android.database.Cursor
import com.example.mobileappth2.data.db.AppDatabase
import com.example.mobileappth2.data.model.WorkLog
import com.example.mobileappth2.data.model.WorkStatus
import java.util.Locale

// Owner: Member B
class WorkLogRepository(private val db: AppDatabase) {

    fun getById(id: Long): WorkLog? {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM WorkLog WHERE id = ?",
            arrayOf(id.toString())
        )
        return cursor.use { if (it.moveToFirst()) fromCursor(it) else null }
    }

    fun getByEmployeeAndDate(employeeId: Long, workDate: String): WorkLog? {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM WorkLog WHERE employeeId = ? AND workDate = ? ORDER BY id DESC LIMIT 1",
            arrayOf(employeeId.toString(), workDate)
        )
        return cursor.use { if (it.moveToFirst()) fromCursor(it) else null }
    }

    fun getActiveCheckIn(employeeId: Long): WorkLog? {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM WorkLog WHERE employeeId = ? AND status = ? ORDER BY id DESC LIMIT 1",
            arrayOf(employeeId.toString(), WorkStatus.CHECKED_IN)
        )
        return cursor.use { if (it.moveToFirst()) fromCursor(it) else null }
    }

    fun listByEmployee(employeeId: Long): List<WorkLog> {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM WorkLog WHERE employeeId = ? ORDER BY workDate DESC, id DESC",
            arrayOf(employeeId.toString())
        )
        return cursor.use { readList(it) }
    }

    // Required contract for Member C
    fun listByEmployeeAndMonth(employeeId: Long, month: Int, year: Int): List<WorkLog> {
        val monthPrefix = String.format(Locale.US, "%04d-%02d", year, month)
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM WorkLog WHERE employeeId = ? AND workDate LIKE ? ORDER BY workDate ASC, id ASC",
            arrayOf(employeeId.toString(), "$monthPrefix%")
        )
        return cursor.use { readList(it) }
    }

    fun getIncompleteLogs(employeeId: Long, todayDate: String): List<WorkLog> {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM WorkLog WHERE employeeId = ? AND workDate < ? AND status = ? ORDER BY workDate ASC",
            arrayOf(employeeId.toString(), todayDate, WorkStatus.CHECKED_IN)
        )
        return cursor.use { readList(it) }
    }

    fun getEmployeeIdByAccountId(accountId: Long): Long? {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT id FROM Employee WHERE accountId = ? LIMIT 1",
            arrayOf(accountId.toString())
        )
        return cursor.use { if (it.moveToFirst()) it.getLong(0) else null }
    }

    fun insert(workLog: WorkLog): Long {
        val values = ContentValues().apply {
            put("employeeId", workLog.employeeId)
            put("workDate", workLog.workDate)
            put("checkInTime", workLog.checkInTime)
            put("checkOutTime", workLog.checkOutTime)
            put("totalHours", workLog.totalHours)
            put("status", workLog.status)
        }
        return db.writableDatabase.insert("WorkLog", null, values)
    }

    fun update(workLog: WorkLog): Int {
        val values = ContentValues().apply {
            put("employeeId", workLog.employeeId)
            put("workDate", workLog.workDate)
            put("checkInTime", workLog.checkInTime)
            put("checkOutTime", workLog.checkOutTime)
            put("totalHours", workLog.totalHours)
            put("status", workLog.status)
        }
        return db.writableDatabase.update(
            "WorkLog",
            values,
            "id = ?",
            arrayOf(workLog.id.toString())
        )
    }

    fun delete(id: Long): Int {
        return db.writableDatabase.delete(
            "WorkLog",
            "id = ?",
            arrayOf(id.toString())
        )
    }

    private fun readList(cursor: Cursor): List<WorkLog> {
        val list = mutableListOf<WorkLog>()
        while (cursor.moveToNext()) {
            list.add(fromCursor(cursor))
        }
        return list
    }

    private fun fromCursor(cursor: Cursor): WorkLog {
        val checkInIdx = cursor.getColumnIndexOrThrow("checkInTime")
        val checkOutIdx = cursor.getColumnIndexOrThrow("checkOutTime")

        return WorkLog(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            employeeId = cursor.getLong(cursor.getColumnIndexOrThrow("employeeId")),
            workDate = cursor.getString(cursor.getColumnIndexOrThrow("workDate")),
            checkInTime = if (cursor.isNull(checkInIdx)) null else cursor.getLong(checkInIdx),
            checkOutTime = if (cursor.isNull(checkOutIdx)) null else cursor.getLong(checkOutIdx),
            totalHours = cursor.getDouble(cursor.getColumnIndexOrThrow("totalHours")),
            status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
        )
    }
}
