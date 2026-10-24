package com.example.mobileappth2.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mobileappth2.data.model.Role

class AppDatabase(context: Context) : SQLiteOpenHelper(context, "workhours.db", null, 1) {

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

    // Seed: admin/admin123 and emp01/123456 (linked to employee EMP01)
    private fun seed(db: SQLiteDatabase) {
        db.insert("Account", null, ContentValues().apply {
            put("username", "admin"); put("password", "admin123"); put("role", Role.ADMIN)
        })
        val accountId = db.insert("Account", null, ContentValues().apply {
            put("username", "emp01"); put("password", "123456"); put("role", Role.EMPLOYEE)
        })
        db.insert("Employee", null, ContentValues().apply {
            put("code", "EMP01"); put("fullname", "Nguyen Van A"); put("email", "a@example.com")
            put("phone", "0900000001"); put("hourlyRate", 50000.0); put("accountId", accountId)
        })
    }
}
