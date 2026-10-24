package com.example.mobileappth2.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobileappth2.AppContainer
import com.example.mobileappth2.data.model.WorkLog
import com.example.mobileappth2.data.model.WorkStatus
import com.example.mobileappth2.ui.nav.Navigator
import com.example.mobileappth2.ui.nav.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Owner: Member B
@Composable
fun EmployeeHomeScreen(container: AppContainer, nav: Navigator) {
    val username = container.session.username() ?: "Employee"
    val accountId = container.session.accountId()

    var todayLog by remember { mutableStateOf<WorkLog?>(null) }
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    LaunchedEffect(Unit) {
        val empId = container.workLogs.getEmployeeIdByAccountId(accountId)
        if (empId != null) {
            todayLog = container.workLogs.getByEmployeeAndDate(empId, todayDate)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Employee Portal",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Welcome, $username!",
            style = MaterialTheme.typography.bodyLarge
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Today's Status ($todayDate):",
                    style = MaterialTheme.typography.titleSmall
                )
                val statusText = when (todayLog?.status) {
                    WorkStatus.CHECKED_IN -> "Currently working (Checked-in)"
                    WorkStatus.COMPLETED -> "Completed (${todayLog?.totalHours ?: 0.0} hours)"
                    else -> "Not checked in yet"
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (todayLog?.status) {
                        WorkStatus.CHECKED_IN -> MaterialTheme.colorScheme.primary
                        WorkStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.outline
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { nav.push(Screen.Attendance) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Chấm công (Check In / Check Out)")
        }

        Button(
            onClick = { nav.push(Screen.AttendanceHistory) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lịch sử chấm công (Attendance History)")
        }

        Button(
            onClick = { nav.push(Screen.MySalary) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Xem bảng lương (My Salary)")
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                container.session.clear()
                nav.reset(Screen.Login)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Đăng xuất (Logout)")
        }
    }
}
