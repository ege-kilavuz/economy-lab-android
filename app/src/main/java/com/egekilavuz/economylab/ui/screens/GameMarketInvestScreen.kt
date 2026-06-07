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
fun GameMarketScreen(vm: GameViewModel, onBack: () -> Unit) {
    val g = vm.gameState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛒 Market", fontWeight = FontWeight.Bold) },
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
                    Text("🛒 Market", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    ActionBtn("Market alışverişi yap") { vm.act(ActionId.Grocery) }
                    ActionBtn("Sinema (eğlence)") { vm.act(ActionId.Cinema) }
                    ActionBtn("Tasarruf: ışıkları kapat") { vm.act(ActionId.LightsOff) }
                    Spacer(Modifier.height(8.dp))
                    Text("Dolap: ${g.fridge}% ${statusText(g.fridge, 30, 55)}", style = MaterialTheme.typography.bodySmall)
                    Text("Moral: ${g.mood}% ${statusText(g.mood, 45, 60)}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameInvestScreen(vm: GameViewModel, onBack: () -> Unit) {
    val g = vm.gameState
    val assets = listOf(HoldingId.Gold, HoldingId.Usd, HoldingId.Btc, HoldingId.Eth, HoldingId.Stock)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📈 Yatırım", fontWeight = FontWeight.Bold) },
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

            // Portfolio
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("📈 Portföy", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    assets.forEach { h ->
                        val qty = g.holdings[h] ?: 0
                        val price = assetPrice(g, h)
                        val value = qty * price
                        Text("${holdingLabel(h)}: $qty adet × ${price} TL = $value TL",
                            style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Toplam: ${totalAssetValue(g)} TL", fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Buy/Sell
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Al / Sat", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    assets.forEach { h ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            ActionBtn("Al ${holdingLabel(h)}") { vm.act(ActionId.BuyAsset, asset = h) }
                            if ((g.holdings[h] ?: 0) > 0) {
                                ActionBtn("Sat ${holdingLabel(h)}") { vm.act(ActionId.SellAsset, asset = h) }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Politika faizi: ${g.policyRate / 100f}%", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun statusText(value: Int, low: Int, med: Int): String = when {
    value < low -> "⚠️ düşük"
    value < med -> "─ orta"
    else -> "✅ iyi"
}

private fun holdingLabel(h: HoldingId): String = when (h) {
    HoldingId.Gold -> "Altın"
    HoldingId.Usd -> "Dolar"
    HoldingId.Btc -> "BTC"
    HoldingId.Eth -> "ETH"
    HoldingId.Stock -> "Hisse"
}
