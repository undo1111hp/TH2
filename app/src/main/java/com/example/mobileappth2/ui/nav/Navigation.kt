package com.example.mobileappth2.ui.nav

import androidx.compose.runtime.mutableStateListOf

// To add a screen: add it here, then add a branch in AppNavHost.
sealed interface Screen {
    data object Login : Screen
    data object AdminHome : Screen
    data object EmployeeHome : Screen
    data object EmployeeList : Screen
    data class EmployeeForm(val id: Long?) : Screen   // null = add new
    data object Attendance : Screen
    data object AttendanceHistory : Screen
    data object MySalary : Screen
    data object SalaryManagement : Screen
}

class Navigator(start: Screen) {
    private val stack = mutableStateListOf(start)
    val current: Screen get() = stack.last()
    val canGoBack: Boolean get() = stack.size > 1

    fun push(screen: Screen) { stack.add(screen) }
    fun pop() { if (canGoBack) stack.removeAt(stack.lastIndex) }

    // Clear the back stack (after login / logout)
    fun reset(screen: Screen) { stack.clear(); stack.add(screen) }
}
