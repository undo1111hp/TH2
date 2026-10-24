package com.example.mobileappth2.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mobileappth2.AppContainer
import com.example.mobileappth2.data.model.Role
import com.example.mobileappth2.ui.nav.Navigator
import com.example.mobileappth2.ui.nav.Screen

// Owner: Member A
@Composable
fun LoginScreen(container: AppContainer, nav: Navigator) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    fun login() {
        if (username.isBlank() || password.isBlank()) {
            error = "Please enter username and password"
            return
        }
        val account = container.accounts.login(username.trim(), password)
        if (account == null) {
            error = "Wrong username or password"
            return
        }
        container.session.saveLogin(account)
        nav.reset(if (account.role == Role.ADMIN) Screen.AdminHome else Screen.EmployeeHome)
    }

    Column(
        Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )
        if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        Button(onClick = { login() }, modifier = Modifier.fillMaxWidth()) { Text("Login") }
    }
}
