package com.devryonlabs.stockcard.ui.components

import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.BorderStroke

@Composable
fun StockOutContent(
    item: StockItem,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Stock Out",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            IconButton(onClick = onDismiss) {
                Text("✕", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Box Info Barang (Abu-abu Muda) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Box Putih
                Surface(
                    modifier = Modifier.size(48.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("📦", fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "Tersedia: ${item.stock} unit",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- Label Counter ---
        Text(
            text = "JUMLAH KELUAR",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.LightGray,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Tombol Minus
            OutlinedIconButton(
                onClick = { if (quantity > 1) quantity-- },
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.5.dp, Color.Black)
            ) {
                Text("-", fontSize = 28.sp, color = Color.Black, fontWeight = FontWeight.Normal)
            }

            Text(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = 40.dp),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            OutlinedIconButton(
                onClick = { if (quantity < item.stock) quantity++ },
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.5.dp, Color(0xFF7B61FF))
            ) {
                Text("+", fontSize = 28.sp, color = Color(0xFF7B61FF), fontWeight = FontWeight.Normal)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { onConfirm(quantity) },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131517)),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "Konfirmasi Pengeluaran",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}