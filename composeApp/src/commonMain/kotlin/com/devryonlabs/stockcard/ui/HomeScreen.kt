package com.devryonlabs.stockcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devryonlabs.stockcard.data.model.StockItem
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

// Data class untuk body request sesuai logic API Next.js
@Serializable
data class StockOutRequest(
    val id: String,
    val quantity: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val userName = remember { TokenManager.getUserName() }
    val scope = rememberCoroutineScope()

    var inventoryList by remember { mutableStateOf(listOf<StockItem>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showStockOutSheet by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<StockItem?>(null) }
    val sheetState = rememberModalBottomSheetState()

    val client = remember {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    val refreshData = {
        scope.launch {
            isLoading = true
            try {
                // Gunakan header auth juga saat fetch jika API stocks kamu diproteksi
                val response: List<StockItem> = client.get("http://10.10.10.13:3000/api/stocks") {
                    header("Authorization", "Bearer ${TokenManager.getToken()}")
                }.body()
                inventoryList = response
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Gagal memuat data"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { refreshData() }

    Scaffold(containerColor = Color(0xFFF8F9FA)) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Header Dashboard
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Hi, $userName!", fontSize = 14.sp, color = Color.Gray)
                    Text("Stock Inventory", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = {
                    TokenManager.clearToken()
                    onLogout()
                }) {
                    Text("LOGOUT →", color = Color(0xFFE91E63), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7B61FF))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(inventoryList) { item ->
                        InventoryCard(item = item, onClick = {
                            selectedItem = item
                            showStockOutSheet = true
                        })
                    }
                }
            }
        }

        if (showStockOutSheet && selectedItem != null) {
            ModalBottomSheet(
                onDismissRequest = { showStockOutSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                StockOutContent(
                    item = selectedItem!!,
                    onDismiss = { showStockOutSheet = false },
                    onConfirm = { qty ->
                        scope.launch {
                            try {
                                // SESUAI LOGIC API: Menggunakan POST dan header Authorization
                                val response = client.post("http://10.10.10.13:3000/api/mobile/stock/out") {
                                    contentType(ContentType.Application.Json)
                                    header("Authorization", "Bearer ${TokenManager.getToken()}")
                                    setBody(StockOutRequest(id = selectedItem!!.id, quantity = qty))
                                }

                                if (response.status == HttpStatusCode.OK) {
                                    showStockOutSheet = false
                                    refreshData()
                                }
                            } catch (e: Exception) {
                                // Handle error koneksi
                            }
                        }
                    }
                )
            }
        }
    }
}

// Komponen InventoryCard & StockOutContent tetap sama (Pastikan StockOutContent menerima onDismiss)
@Composable
fun StockOutContent(item: StockItem, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var quantity by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Stock Out", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDismiss) {
                Text("✕", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(48.dp), color = Color.White, shape = RoundedCornerShape(12.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text("📦", fontSize = 20.sp) }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Tersedia: ${item.stock} unit", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("JUMLAH KELUAR", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedIconButton(
                onClick = { if (quantity > 1) quantity-- },
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(50)
            ) { Text("-", fontSize = 20.sp) }

            Text(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = 32.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { if (quantity < item.stock) quantity++ },
                modifier = Modifier.size(48.dp).border(1.dp, Color(0xFF7B61FF), RoundedCornerShape(50))
            ) { Text("+", fontSize = 20.sp, color = Color(0xFF7B61FF)) }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onConfirm(quantity) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1C1E)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Konfirmasi Pengeluaran", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun InventoryCard(item: StockItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Kategori: ${item.category}", fontSize = 14.sp, color = Color.LightGray)
            }
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Stock: ${item.stock} Pcs", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Qty", color = Color(0xFF1976D2), fontSize = 10.sp)
                }
            }
        }
    }
}