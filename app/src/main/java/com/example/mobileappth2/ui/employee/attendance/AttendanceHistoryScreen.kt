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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobileappth2.AppContainer
import com.example.mobileappth2.data.model.WorkLog
import com.example.mobileappth2.data.model.WorkStatus
import com.example.mobileappth2.domain.HoursCalculator
import com.example.mobileappth2.ui.nav.Navigator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.round

// Owner: Member B
@Composable
fun AttendanceHistoryScreen(container: AppContainer, nav: Navigator) {
    val accountId = container.session.accountId()
    val calendar = remember { Calendar.getInstance() }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }
    var logs by remember { mutableStateOf<List<WorkLog>>(emptyList()) }

    fun loadMonthData() {
        val empId = container.workLogs.getEmployeeIdByAccountId(accountId) ?: return
        logs = container.workLogs.listByEmployeeAndMonth(empId, selectedMonth, selectedYear)
    }

    LaunchedEffect(selectedMonth, selectedYear) {
        loadMonthData()
    }

    val totalHours = remember(logs) {
        val sum = logs.sumOf { it.totalHours }
        round(sum * 100.0) / 100.0
    }
    val totalOvertime = remember(logs) {
        val sum = logs.sumOf { HoursCalculator.calculateOvertimeHours(it.totalHours) }
        round(sum * 100.0) / 100.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Lịch sử chấm công", style = MaterialTheme.typography.headlineMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                if (selectedMonth == 1) {
                    selectedMonth = 12
                    selectedYear -= 1
                } else {
                    selectedMonth -= 1
                }
            }) { Text("<") }

            Text(
                text = String.format(Locale.getDefault(), "Tháng %02d / %d", selectedMonth, selectedYear),
                style = MaterialTheme.typography.titleMedium
            )

            Button(onClick = {
                if (selectedMonth == 12) {
                    selectedMonth = 1
                    selectedYear += 1
                } else {
                    selectedMonth += 1
                }
            }) { Text(">") }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Tổng kết tháng:", style = MaterialTheme.typography.titleSmall)
                Text("Số ngày làm việc: ${logs.size} ngày")
                Text("Tổng giờ làm: $totalHours giờ")
                Text("Tổng giờ tăng ca (>8h/ngày): $totalOvertime giờ")
            }
        }

        if (logs.isEmpty()) {
            Text(
                text = "Không có bản ghi chấm công nào trong tháng này.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(logs) { log ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(log.workDate, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    text = if (log.status == WorkStatus.COMPLETED) "Hoàn thành" else "Đang làm",
                                    color = if (log.status == WorkStatus.COMPLETED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                            val inStr = log.checkInTime?.let { timeFormat.format(Date(it)) } ?: "--:--"
                            val outStr = log.checkOutTime?.let { timeFormat.format(Date(it)) } ?: "--:--"
                            Text("Vào: $inStr - Ra: $outStr | Tổng: ${log.totalHours}h")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { nav.pop() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
