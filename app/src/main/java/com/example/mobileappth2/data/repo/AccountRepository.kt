package com.example.mobileappth2.data.repo

import com.example.mobileappth2.data.db.AppDatabase
import com.example.mobileappth2.data.model.Account

// Owner: Member A
class AccountRepository(private val db: AppDatabase) {

    // Returns the matching account, or null if username/password is wrong
    fun login(username: String, password: String): Account? =
        db.readableDatabase.rawQuery(
            "SELECT id, username, password, role FROM Account WHERE username = ? AND password = ?",
            arrayOf(username, password),
        ).use {
            if (it.moveToFirst()) Account(it.getLong(0), it.getString(1), it.getString(2), it.getString(3)) else null
        }
}
