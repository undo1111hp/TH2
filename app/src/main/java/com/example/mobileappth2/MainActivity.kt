package com.example.mobileappth2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import com.example.mobileappth2.data.model.Role
import com.example.mobileappth2.ui.nav.AppNavHost
import com.example.mobileappth2.ui.nav.Navigator
import com.example.mobileappth2.ui.nav.Screen
import com.example.mobileappth2.ui.theme.MobileAppTH2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = AppContainer(this)
        // Skip login if a session is saved
        val start = when {
            !container.session.isLoggedIn() -> Screen.Login
            container.session.role() == Role.ADMIN -> Screen.AdminHome
            else -> Screen.EmployeeHome
        }
        setContent {
            MobileAppTH2Theme {
                Surface { AppNavHost(container, remember { Navigator(start) }) }
            }
        }
    }
}
