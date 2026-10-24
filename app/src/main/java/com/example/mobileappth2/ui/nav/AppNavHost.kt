package com.example.mobileappth2.ui.nav

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.example.mobileappth2.AppContainer
import com.example.mobileappth2.ui.admin.employee.EmployeeFormScreen
import com.example.mobileappth2.ui.admin.employee.EmployeeListScreen
import com.example.mobileappth2.ui.admin.salary.SalaryManagementScreen
import com.example.mobileappth2.ui.auth.LoginScreen
import com.example.mobileappth2.ui.employee.attendance.AttendanceHistoryScreen
import com.example.mobileappth2.ui.employee.attendance.AttendanceScreen
import com.example.mobileappth2.ui.employee.salary.MySalaryScreen
import com.example.mobileappth2.ui.home.AdminHomeScreen
import com.example.mobileappth2.ui.home.EmployeeHomeScreen

@Composable
fun AppNavHost(container: AppContainer, nav: Navigator) {
    BackHandler(enabled = nav.canGoBack) { nav.pop() }
    when (val screen = nav.current) {
        Screen.Login -> LoginScreen(container, nav)
        Screen.AdminHome -> AdminHomeScreen(container, nav)
        Screen.EmployeeHome -> EmployeeHomeScreen(container, nav)
        Screen.EmployeeList -> EmployeeListScreen(container, nav)
        is Screen.EmployeeForm -> EmployeeFormScreen(container, nav, screen.id)
        Screen.Attendance -> AttendanceScreen(container, nav)
        Screen.AttendanceHistory -> AttendanceHistoryScreen(container, nav)
        Screen.MySalary -> MySalaryScreen(container, nav)
        Screen.SalaryManagement -> SalaryManagementScreen(container, nav)
    }
}
