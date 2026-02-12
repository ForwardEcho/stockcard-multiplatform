package com.devryonlabs.stockcard.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devryonlabs.stockcard.utils.TokenManager
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.devryonlabs.stockcard.data.model.LoginResponse

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Inisialisasi Ktor Client
    val client = remember {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        // --- Background Ornaments ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF7B61FF),
                radius = 150.dp.toPx(),
                center = Offset(0f, 200.dp.toPx())
            )
            drawCircle(
                color = Color(0xFF7B61FF),
                radius = 180.dp.toPx(),
                center = Offset(size.width, size.height - 100.dp.toPx())
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "StockCard",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp
                ),
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Welcome Back", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    if (errorMessage != null) {
                        Text(errorMessage!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isLoading,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(if (passwordVisible) "HIDE" else "SHOW", fontSize = 10.sp)
                            }
                        }
                    )

                    Text(
                        "Lupa Password?",
                        modifier = Modifier.align(Alignment.End).padding(top = 8.dp),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                isLoading = true
                                errorMessage = null
                                scope.launch {
                                    try {
                                        val response: LoginResponse = client.post("http://10.10.10.13:3000/api/mobile-login/login") {
                                            contentType(ContentType.Application.Json)
                                            setBody(mapOf("email" to email, "password" to password))
                                        }.body()

                                        if (response.success && response.token != null) {
                                            TokenManager.saveToken(response.token)
                                            TokenManager.saveUserName(response.user?.name ?: "User")
                                            onLoginSuccess()
                                        } else {
                                            errorMessage = response.message ?: "Login Gagal"
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Koneksi Error: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Masuk Ke Akun", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Belum punya akun? Daftar Sekarang",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = 12.sp,
                        color = Color(0xFF7B61FF),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}