package com.example.mobileappth2.session

import android.content.Context
import com.example.mobileappth2.data.model.Account

// Owner: Member A
class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveLogin(account: Account) {
        prefs.edit()
            .putLong("accountId", account.id)
            .putString("username", account.username)
            .putString("role", account.role)
            .apply()
    }

    fun isLoggedIn() = accountId() != -1L
    fun accountId() = prefs.getLong("accountId", -1L)
    fun username(): String? = prefs.getString("username", null)
    fun role(): String? = prefs.getString("role", null)
    fun clear() = prefs.edit().clear().apply()
}
