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
fun GameNewsScreen(vm: GameViewModel, onBack: () -> Unit) {
    val g = vm.gameState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📰 Haber", fontWeight = FontWeight.Bold) },
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

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("📰 Haberler", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    // Show last 10 log entries related to news
                    g.log.filter { it.contains("Haber") || it.contains("TCMB") || it.contains("Enflasyon") }.take(5).forEach { entry ->
                        Text("• $entry", style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameEndScreen(vm: GameViewModel, onBack: () -> Unit) {
    val g = vm.gameState
    val score = scoreEndOfMonth(g)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Ay Sonu", fontWeight = FontWeight.Bold) },
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
            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📊 Ay Sonu Raporu", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Skor: $score/200", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Mod: ${difficultyLabel(g.difficulty)} · Puan: ${g.points}")
                    Spacer(Modifier.height(8.dp))
                    Text("Nakit: ${g.cash} TL")
                    Text("Kart borcu: ${g.cardDebt} TL")
                    Text("Dolap: ${g.fridge}%")
                    Spacer(Modifier.height(8.dp))

                    val eval = when {
                        score >= 70 -> "Dengeyi kurdun: ihtiyaçlar + borç kontrolü + birikim."
                        score >= 40 -> "Kıl payı: bazı alanlarda açık var."
                        else -> "Zorlu ay: borç/ödeme planı bozuldu."
                    }
                    Text(eval, style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { vm.startNewGame(g.difficulty) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Tekrar Oyna")
                    }
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                        Text("Telefona dön")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
