package com.example.mobileappth2.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobileappth2.AppContainer
import com.example.mobileappth2.ui.nav.Navigator
import com.example.mobileappth2.ui.nav.Screen

// Owner: Member A
@Composable
fun AdminHomeScreen(container: AppContainer, nav: Navigator) {
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Admin", style = MaterialTheme.typography.headlineMedium)
        Text("Logged in as ${container.session.username()}")
        Button(onClick = { nav.push(Screen.EmployeeList) }, modifier = Modifier.fillMaxWidth()) {
            Text("Employees")
        }
        Button(onClick = { nav.push(Screen.SalaryManagement) }, modifier = Modifier.fillMaxWidth()) {
            Text("Salaries")
        }
        OutlinedButton(
            onClick = {
                container.session.clear()
                nav.reset(Screen.Login)
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Logout") }
    }
}
