package com.devryonlabs.stockcard

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.devryonlabs.stockcard.ui.LoginScreen
import com.devryonlabs.stockcard.utils.TokenManager
import androidx.compose.runtime.Composable
import com.devryonlabs.stockcard.ui.HomeScreen

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf("login") }

    // Cek jika sudah ada token, langsung ke home
    LaunchedEffect(Unit) {
        if (TokenManager.getToken() != null) {
            currentScreen = "home"
        }
    }

    MaterialTheme {
        when (currentScreen) {
            "login" -> LoginScreen(onLoginSuccess = { currentScreen = "home" })
            "home" -> HomeScreen(onLogout = { currentScreen = "login" })
        }
    }
}

@Composable
fun MainDashboardScreen(onLogout: () -> Unit) {
    // Tampilan sementara setelah login berhasil
    androidx.compose.foundation.layout.Column {
        androidx.compose.material3.Text("Selamat Datang di Dashboard Stok!")
        androidx.compose.material3.Button(onClick = onLogout) {
            androidx.compose.material3.Text("Logout")
        }
    }
}