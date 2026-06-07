package com.egekilavuz.economylab.ui.simulations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.roundToInt

private fun monthlyPayment(principal: Int, annualRate: Double, months: Int): Double {
    val r = annualRate / 100.0 / 12.0
    return if (r == 0.0) principal.toDouble() / months
    else (principal * r) / (1 - (1 + r).pow(-months))
}

private fun compoundGrowth(principal: Int, annualRate: Double, months: Int): Double {
    val r = annualRate / 100.0 / 12.0
    return principal * (1 + r).pow(months)
}

private data class MonthData(val m: Int, val payment: Double, val remaining: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestSimulation(onBack: () -> Unit) {
    var principal by remember { mutableIntStateOf(100000) }
    var rate by remember { mutableIntStateOf(45) }
    var months by remember { mutableIntStateOf(24) }

    val pay = remember(principal, rate, months) { monthlyPayment(principal, rate.toDouble(), months) }
    val total = remember(pay, months) { pay * months }
    val totalInterest = remember(total, principal) { (total - principal).roundToInt() }
    val compound = remember(principal, rate, months) { compoundGrowth(principal, rate.toDouble(), months) }

    val chartData = remember(principal, rate, months) {
        val r = rate / 100.0 / 12.0
        val payVal = monthlyPayment(principal, rate.toDouble(), months)
        (0..months).map { m ->
            val paid = payVal * m
            val remaining = if (r == 0.0) (principal - principal.toDouble() * m / months).coerceAtLeast(0.0)
            else (principal * ((1 + r).pow(months) - (1 + r).pow(m)) / ((1 + r).pow(months) - 1)).coerceAtLeast(0.0)
            MonthData(m, paid, remaining)
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("💹 Faiz & Kredi", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 12.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            Text("Aynı oran borçta maliyet, birikimde büyüme demek.", style = MaterialTheme.typography.bodySmall)
            SliderCard("Anapara (TL)", principal, 10000, 500000, 5000) { principal = it }
            SliderCard("Yıllık faiz (%)", rate, 5, 80, 1) { rate = it }
            SliderCard("Vade (ay)", months, 6, 60, 1) { months = it }

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(Modifier.padding(14.dp)) {
                    Text("Borç", fontWeight = FontWeight.Bold)
                    Text("Aylık taksit: ${pay.roundToInt()} TL")
                    Text("Toplam geri ödeme: ${total.roundToInt()} TL")
                    Text("Toplam faiz: $totalInterest TL")
                    Text("Birikim", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    Text("Aynı parayı birikime koysan: ${compound.roundToInt()} TL")
                }
            }

            // Bar chart
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(Modifier.padding(14.dp)) {
                    Text("Grafik", fontWeight = FontWeight.Bold)
                    Box(Modifier.fillMaxWidth().height(160.dp)) {
                        Canvas(Modifier.fillMaxSize()) {
                            val w = size.width; val h = size.height
                            val left = 40f; val right = 8f; val top = 8f; val bottom = 24f
                            val cw = w - left - right; val ch = h - top - bottom
                            val maxVal = chartData.maxOfOrNull { it.remaining }?.coerceAtLeast(1.0) ?: 1.0
                            val blue = Color(0xFF1976D2)
                            chartData.forEachIndexed { i, d ->
                                if (i % 3 == 0) {
                                    val x = left + cw * i / (chartData.size - 1).coerceAtLeast(1)
                                    val barH = (d.remaining / maxVal * ch).toFloat()
                                    drawRect(blue, Offset(x - 2f, top + ch - barH), androidx.compose.ui.geometry.Size(4f, barH))
                                }
                            }
                            drawContext.canvas.nativeCanvas.apply {
                                drawText("0a", left, h - 4f, android.graphics.Paint().apply {
                                    color = 0xFF94A3B8.toInt(); textSize = 14f; textAlign = android.graphics.Paint.Align.LEFT
                                })
                                drawText("${months}a", w - right, h - 4f, android.graphics.Paint().apply {
                                    color = 0xFF94A3B8.toInt(); textSize = 14f; textAlign = android.graphics.Paint.Align.RIGHT
                                })
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
