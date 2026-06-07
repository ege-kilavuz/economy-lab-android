package com.egekilavuz.economylab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.egekilavuz.economylab.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameBankScreen(vm: GameViewModel, onBack: () -> Unit) {
    val g = vm.gameState
    val b = balanceFor(g.difficulty)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏦 Banka", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            StatChips(g)
            Spacer(Modifier.height(12.dp))

            // Account summary
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Hesap Özeti", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Nakit: ${g.cash} TL", style = MaterialTheme.typography.bodyMedium)
                    Text("Kart borcu: ${g.cardDebt} TL", style = MaterialTheme.typography.bodyMedium)
                    Text("Maaş (aylık): ${b.startingCash} TL", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Bills & Rent
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Faturalar", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    if (!g.rentPaid) ActionBtn("Kira öde (${g.currentRent} TL)") { vm.act(ActionId.PayRent) }
                    if (!g.billsPaid.dues) ActionBtn("Aidat (${b.dues} TL)") { vm.act(ActionId.PayDues) }
                    if (!g.billsPaid.electric) ActionBtn("Elektrik (${b.electric} TL)") { vm.act(ActionId.PayElectric) }
                    if (!g.billsPaid.gas) ActionBtn("Doğalgaz (${b.gas} TL)") { vm.act(ActionId.PayGas) }
                    if (!g.billsPaid.internet) ActionBtn("İnternet (${b.internet} TL)") { vm.act(ActionId.PayInternet) }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Credit card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Kredi Kartı", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    ActionBtn("Asgari öde") { vm.act(ActionId.PayCardMin) }
                    ActionBtn("Tamamını kapat") { vm.act(ActionId.PayCardAll) }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Status summary
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
            ) {
                Text(
                    text = buildString {
                        append(if (g.rentPaid) "✅ Kira ödendi" else "❌ Kira ödenmedi")
                        append(" · Faturalar: ${listOf(g.billsPaid.electric, g.billsPaid.gas, g.billsPaid.internet, g.billsPaid.dues).count { it }}/4 ödendi")
                        append(" · Kart: ${if (g.cardDebt > 0) "${g.cardDebt} TL borç" else "✅ temiz"}")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ActionBtn(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(text, fontSize = MaterialTheme.typography.bodySmall.fontSize)
    }
}
