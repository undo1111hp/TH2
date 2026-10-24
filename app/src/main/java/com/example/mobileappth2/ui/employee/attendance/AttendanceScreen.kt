package com.example.mobileappth2.ui.employee.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.mobileappth2.domain.AttendanceValidator
import com.example.mobileappth2.domain.HoursCalculator
import com.example.mobileappth2.ui.nav.Navigator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Owner: Member B
@Composable
fun AttendanceScreen(container: AppContainer, nav: Navigator) {
    val accountId = container.session.accountId()
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    var employeeId by remember { mutableStateOf<Long?>(null) }
    var todayLog by remember { mutableStateOf<WorkLog?>(null) }
    var incompleteCount by remember { mutableStateOf(0) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    fun refreshState() {
        val empId = container.workLogs.getEmployeeIdByAccountId(accountId)
        employeeId = empId
        if (empId != null) {
            todayLog = container.workLogs.getByEmployeeAndDate(empId, todayDate)
            val incompleteList = container.workLogs.getIncompleteLogs(empId, todayDate)
            incompleteCount = incompleteList.size
        }
    }

    LaunchedEffect(Unit) {
        refreshState()
    }

    fun handleCheckIn() {
        val empId = employeeId ?: return
        val validation = AttendanceValidator.validateCheckIn(todayLog)
        if (!validation.isValid) {
            feedbackMessage = validation.message
            isError = true
            return
        }

        val now = System.currentTimeMillis()
        val newLog = WorkLog(
            employeeId = empId,
            workDate = todayDate,
            checkInTime = now,
            checkOutTime = null,
            totalHours = 0.0,
            status = WorkStatus.CHECKED_IN
        )
        container.workLogs.insert(newLog)
        refreshState()
        feedbackMessage = "Check-in successful at ${timeFormat.format(Date(now))}!"
        isError = false
    }

    fun handleCheckOut() {
        val current = todayLog ?: return
        val now = System.currentTimeMillis()
        val validation = AttendanceValidator.validateCheckOut(current, now)
        if (!validation.isValid) {
            feedbackMessage = validation.message
            isError = true
            return
        }

        val hours = HoursCalculator.calculateTotalHours(current.checkInTime, now)
        val updated = current.copy(
            checkOutTime = now,
            totalHours = hours,
            status = WorkStatus.COMPLETED
        )
        container.workLogs.update(updated)
        refreshState()
        val overtime = HoursCalculator.calculateOvertimeHours(hours)
        feedbackMessage = "Check-out successful! Total: $hours hrs (Overtime: $overtime hrs)."
        isError = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Chấm công (Attendance)", style = MaterialTheme.typography.headlineMedium)
        Text("Ngày làm việc: $todayDate", style = MaterialTheme.typography.titleMedium)

        if (incompleteCount > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Lưu ý: Bạn có $incompleteCount ngày trước chưa check-out!",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Trạng thái ca làm:", style = MaterialTheme.typography.titleSmall)
                val status = todayLog?.status ?: "Chưa điểm danh"
                Text("Trạng thái: $status", style = MaterialTheme.typography.bodyLarge)

                todayLog?.checkInTime?.let {
                    Text("Check-in: ${timeFormat.format(Date(it))}")
                }
                todayLog?.checkOutTime?.let {
                    Text("Check-out: ${timeFormat.format(Date(it))}")
                    Text("Tổng giờ: ${todayLog?.totalHours ?: 0.0} giờ")
                }
            }
        }

        if (feedbackMessage.isNotEmpty()) {
            Text(
                text = feedbackMessage,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        val canCheckIn = AttendanceValidator.canCheckIn(todayLog)
        val canCheckOut = todayLog?.status == WorkStatus.CHECKED_IN

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { handleCheckIn() },
                enabled = canCheckIn,
                modifier = Modifier.weight(1f)
            ) {
                Text("Check In")
            }
            Button(
                onClick = { handleCheckOut() },
                enabled = canCheckOut,
                modifier = Modifier.weight(1f)
            ) {
                Text("Check Out")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { nav.pop() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
