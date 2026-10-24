package com.example.mobileappth2.ui.admin.salary

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileappth2.AppContainer
import com.example.mobileappth2.data.model.Salary
import com.example.mobileappth2.data.model.SalaryStatus
import com.example.mobileappth2.domain.SalaryCalculator
import com.example.mobileappth2.ui.nav.Navigator
import java.util.Calendar

// Owner: Member C
@Composable
fun SalaryManagementScreen(container: AppContainer, nav: Navigator) {
    val calendar = remember { Calendar.getInstance() }
    var monthInput by remember { mutableStateOf((calendar.get(Calendar.MONTH) + 1).toString()) }
    var yearInput by remember { mutableStateOf(calendar.get(Calendar.YEAR).toString()) }
    var salaryList by remember { mutableStateOf<List<Pair<String, Salary>>>(emptyList()) }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Salary?>(null) }

    fun setMessage(text: String, error: Boolean = false) {
        message = text; isError = error
    }

    fun parseMonthYear(): Pair<Int, Int>? {
        val month = monthInput.toIntOrNull()
        val year = yearInput.toIntOrNull()
        if (month == null || year == null || month !in 1..12 || year < 2000) {
            setMessage("Invalid month (1-12) or year (>= 2000).", true)
            return null
        }
        return month to year
    }

    fun loadList() {
        val (month, year) = parseMonthYear() ?: return
        val records = container.salaries.listByMonth(month, year)
        salaryList = records.map { salary ->
            val emp = container.salaries.getEmployeeById(salary.employeeId)
            val label = emp?.let { "${it.code} - ${it.fullname}" } ?: "Employee #${salary.employeeId}"
            label to salary
        }
        setMessage(if (records.isEmpty()) "No salary records for $month/$year." else "Loaded ${records.size} record(s).")
    }

    fun calculateMonth() {
        val (month, year) = parseMonthYear() ?: return
        val employees = container.salaries.listAllEmployees()
        if (employees.isEmpty()) {
            setMessage("No employees found.", true); return
        }
        var count = 0
        for (emp in employees) {
            val logs = container.workLogs.listByEmployeeAndMonth(emp.id, month, year)
            val result = SalaryCalculator.calculateMonth(logs, emp.hourlyRate)
            if (result.totalHours > 0.0) {
                container.salaries.upsert(
                    Salary(
                        employeeId = emp.id,
                        month = month,
                        year = year,
                        totalHours = result.totalHours,
                        overtimeHours = result.overtimeHours,
                        totalSalary = result.totalSalary,
                    )
                )
                count++
            }
        }
        setMessage("Calculated salary for $count employee(s) in $month/$year.")
        loadList()
    }

    fun markPaid(salary: Salary) {
        container.salaries.updateStatus(salary.id, SalaryStatus.PAID)
        setMessage("Marked as PAID.")
        loadList()
    }

    fun deleteSalary(salary: Salary) {
        container.salaries.delete(salary.id)
        setMessage("Deleted salary record.")
        deleteTarget = null
        loadList()
    }

    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Salary Management", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = monthInput,
                onValueChange = { monthInput = it },
                label = { Text("Month") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = yearInput,
                onValueChange = { yearInput = it },
                label = { Text("Year") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { calculateMonth() }) { Text("Calculate") }
            Button(onClick = { loadList() }) { Text("Load") }
        }

        if (message.isNotEmpty()) {
            Text(message, color = if (isError) Color.Red else Color.Unspecified)
        }

        Spacer(Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(salaryList) { (label, salary) ->
                SalaryRow(label, salary,
                    onMarkPaid = { markPaid(salary) },
                    onDelete = { deleteTarget = salary }
                )
            }
        }

        Button(onClick = { nav.pop() }) { Text("Back") }
    }

    // Delete confirmation dialog
    deleteTarget?.let { salary ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete Salary?") },
            text = { Text("Remove salary record for ${salary.month}/${salary.year}?") },
            confirmButton = {
                TextButton(onClick = { deleteSalary(salary) }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SalaryRow(
    label: String,
    salary: Salary,
    onMarkPaid: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text("Hours: ${salary.totalHours}  |  OT: ${salary.overtimeHours}  |  Salary: ${salary.totalSalary}")
        Text("Status: ${salary.status}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (salary.status == SalaryStatus.PENDING) {
                Button(onClick = onMarkPaid) { Text("Mark PAID") }
            }
            Button(onClick = onDelete) { Text("Delete") }
        }
    }
}
