package com.example.mobileappth2.ui.employee.salary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileappth2.AppContainer
import com.example.mobileappth2.data.model.Salary
import com.example.mobileappth2.ui.nav.Navigator
import java.util.Calendar

// Owner: Member C
@Composable
fun MySalaryScreen(container: AppContainer, nav: Navigator) {
    val calendar = remember { Calendar.getInstance() }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var salaryList by remember { mutableStateOf<List<Salary>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    val accountId = container.session.accountId()
    val employee = remember { container.salaries.getEmployeeByAccountId(accountId) }

    if (employee == null) {
        Column(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp)) {
            Text("Error: No employee linked to this account.", color = Color.Red)
            Button(onClick = { nav.pop() }) { Text("Back") }
        }
        return
    }

    // Load salary records for this employee
    fun loadSalaries() {
        salaryList = container.salaries.listByEmployee(employee.id)
        errorMessage = ""
    }

    // Initial load
    remember { loadSalaries(); true }

    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("My Salary", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Employee: ${employee.fullname} (${employee.code})")
        Text("Hourly rate: ${employee.hourlyRate}")

        Spacer(Modifier.height(8.dp))

        if (salaryList.isEmpty()) {
            Text("No salary records found.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(salaryList) { salary ->
                    SalaryItem(salary)
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red)
        }

        Button(onClick = { nav.pop() }) { Text("Back") }
    }
}

@Composable
private fun SalaryItem(salary: Salary) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            "${salary.month}/${salary.year} — ${salary.status}",
            fontWeight = FontWeight.SemiBold
        )
        Text("Total hours: ${salary.totalHours}  |  Overtime: ${salary.overtimeHours}")
        Text("Total salary: ${salary.totalSalary}")
    }
}
