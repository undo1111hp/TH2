package com.example.mobileappth2.ui.admin.employee

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobileappth2.AppContainer
import com.example.mobileappth2.ui.nav.Navigator

// Owner: Member A
// TODO: add/update employee incl. hourlyRate (id == null means add)
@Composable
fun EmployeeFormScreen(container: AppContainer, nav: Navigator, id: Long?) {
    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp)) {
        Text("EmployeeFormScreen")
        Button(onClick = { nav.pop() }) { Text("Back") }
    }
}
