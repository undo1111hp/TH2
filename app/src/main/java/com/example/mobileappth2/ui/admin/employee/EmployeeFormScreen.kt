package com.example.mobileappth2.ui.admin.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mobileappth2.AppContainer
import com.example.mobileappth2.data.model.Employee
import com.example.mobileappth2.ui.nav.Navigator

// Owner: Member A. id == null means add a new employee (with a login account), otherwise edit.
@Composable
fun EmployeeFormScreen(container: AppContainer, nav: Navigator, id: Long?) {
    val existing = remember { id?.let { container.employees.getById(it) } }
    var code by remember { mutableStateOf(existing?.code ?: "") }
    var fullname by remember { mutableStateOf(existing?.fullname ?: "") }
    var email by remember { mutableStateOf(existing?.email ?: "") }
    var phone by remember { mutableStateOf(existing?.phone ?: "") }
    var hourlyRate by remember { mutableStateOf(existing?.hourlyRate?.toString() ?: "") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    fun save() {
        val rate = hourlyRate.toDoubleOrNull()
        error = when {
            code.isBlank() || fullname.isBlank() -> "Code and full name are required"
            rate == null || rate <= 0 -> "Hourly rate must be a number greater than 0"
            existing == null && (username.isBlank() || password.isBlank()) -> "Username and password are required"
            else -> ""
        }
        if (error.isNotEmpty() || rate == null) return

        val employee = Employee(
            id = existing?.id ?: 0,
            code = code.trim(),
            fullname = fullname.trim(),
            email = email.trim(),
            phone = phone.trim(),
            hourlyRate = rate,
            accountId = existing?.accountId ?: 0,
        )
        val saved = if (existing == null) {
            container.employees.insert(employee, username.trim(), password) != -1L
        } else {
            container.employees.update(employee)
        }
        if (saved) nav.pop() else error = "Code or username already exists"
    }

    Column(
        Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(if (existing == null) "Add employee" else "Edit employee", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(code, { code = it }, label = { Text("Code") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(fullname, { fullname = it }, label = { Text("Full name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            email, { email = it }, label = { Text("Email") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            phone, { phone = it }, label = { Text("Phone") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            hourlyRate, { hourlyRate = it }, label = { Text("Hourly rate") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(),
        )
        if (existing == null) {
            OutlinedTextField(username, { username = it }, label = { Text("Login username") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(password, { password = it }, label = { Text("Login password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { save() }) { Text("Save") }
            Button(onClick = { nav.pop() }) { Text("Cancel") }
        }
    }
}
