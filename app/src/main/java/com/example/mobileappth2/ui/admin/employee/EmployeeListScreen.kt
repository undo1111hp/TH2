package com.example.mobileappth2.ui.admin.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobileappth2.AppContainer
import com.example.mobileappth2.data.model.Employee
import com.example.mobileappth2.ui.nav.Navigator
import com.example.mobileappth2.ui.nav.Screen

// Owner: Member A
@Composable
fun EmployeeListScreen(container: AppContainer, nav: Navigator) {
    var query by remember { mutableStateOf("") }
    var reload by remember { mutableIntStateOf(0) }       // bump to re-query after a delete
    var toDelete by remember { mutableStateOf<Employee?>(null) }
    val employees = remember(query, reload) { container.employees.search(query) }

    Column(
        Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Employees", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search by code or name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { nav.push(Screen.EmployeeForm(null)) }) { Text("Add") }
            Button(onClick = { nav.pop() }) { Text("Back") }
        }
        if (employees.isEmpty()) Text("No employees found")
        LazyColumn(Modifier.weight(1f)) {
            items(employees, key = { it.id }) { e ->
                Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("${e.code} - ${e.fullname}")
                    Text("${e.email} | ${e.phone} | ${e.hourlyRate} per hour")
                    Row {
                        TextButton(onClick = { nav.push(Screen.EmployeeForm(e.id)) }) { Text("Edit") }
                        TextButton(onClick = { toDelete = e }) { Text("Delete") }
                    }
                    HorizontalDivider()
                }
            }
        }
    }

    toDelete?.let { e ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Delete ${e.fullname}?") },
            text = { Text("This also deletes their account, attendance and salary records.") },
            confirmButton = {
                TextButton(onClick = {
                    container.employees.delete(e.id)
                    toDelete = null
                    reload++
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancel") } },
        )
    }
}
