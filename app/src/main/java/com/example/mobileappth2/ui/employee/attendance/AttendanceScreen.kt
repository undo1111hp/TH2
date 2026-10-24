package com.example.mobileappth2.ui.employee.attendance

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

// Owner: Member B
// TODO: check in / check out
@Composable
fun AttendanceScreen(container: AppContainer, nav: Navigator) {
    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp)) {
        Text("AttendanceScreen")
        Button(onClick = { nav.pop() }) { Text("Back") }
    }
}
