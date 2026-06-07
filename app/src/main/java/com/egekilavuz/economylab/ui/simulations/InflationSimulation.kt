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

data class InflationPoint(val m: Int, val basket: Double, val realIncomeIndex: Double)

fun computeInflationSeries(
    months: Int, monthlyIncome: Int, inflationPct: Double, basket0: Int, incomeIndex: Int
): List<InflationPoint> {
    val inflation = inflationPct / 100.0
    return (0..months).map { m ->
        val basket = basket0 * (1 + inflation).pow(m)
        val incomeGrowth = 1 + inflation * (incomeIndex / 100.0)
        val nominalIncome = monthlyIncome * (incomeGrowth).pow(m)
        val realIncome = nominalIncome / (1 + inflation).pow(m)
        val realIncomeIdx = (realIncome / monthlyIncome) * 100
        InflationPoint(m, basket, realIncomeIdx)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InflationSimulation(onBack: () -> Unit) {
    var monthlyIncome by remember { mutableIntStateOf(25000) }
    var basket0 by remember { mutableIntStateOf(12000) }
    var inflationPct by remember { mutableFloatStateOf(3.0f) }
    var months by remember { mutableIntStateOf(24) }
    var incomeIndex by remember { mutableIntStateOf(0) }

    val data = remember(monthlyIncome, basket0, inflationPct, months, incomeIndex) {
        computeInflationSeries(months, monthlyIncome, inflationPct.toDouble(), basket0, incomeIndex)
    }

    val basketEnd = data.lastOrNull()?.basket ?: basket0.toDouble()
    val realEnd = data.lastOrNull()?.realIncomeIndex ?: 100.0

    val headline = when {
        realEnd >= 90 -> "Alım gücü küçük bir düşüş yaşadı."
        realEnd >= 70 -> "Alım gücü belirgin şekilde eridi."
        else -> "Alım gücü ciddi şekilde eridi."
    }
    val feeling = when {
        realEnd >= 90 -> "Hissiyat: dengeli — küçük ayarlamalar yeter."
        realEnd >= 70 -> "Hissiyat: baskı artıyor — gideri optimize et, ek gelir ara."
        else -> "Hissiyat: stres yüksek — sepeti küçültmek veya gelir artırmak şart."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📈 Enflasyon Lab", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") }
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
            Spacer(Modifier.height(8.dp))

            // Description
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Enflasyon Laboratuvarı", fontWeight = FontWeight.Bold)
                    Text("Sepet fiyatı artarken maaş aynı kalırsa, alım gücü nasıl değişir?",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Sliders
            SliderCard("Aylık gelir (TL)", monthlyIncome, 5000, 100000, 500) { monthlyIncome = it }
            SliderCard("Başlangıç sepeti (TL)", basket0, 1000, 60000, 250) { basket0 = it }
            SliderCard("Aylık enflasyon (%)", (inflationPct * 10).roundToInt(), 0, 150, 1) { inflationPct = it / 10f }
            SliderCard("Maaş artış endeksi (%)", incomeIndex, 0, 100, 5) { incomeIndex = it }
            SliderCard("Süre (ay)", months, 6, 60, 1) { months = it }

            Spacer(Modifier.height(8.dp))

            // Result
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Sonuç", fontWeight = FontWeight.Bold)
                    Text("$headline ${months}. ayda sepet ~ ${basketEnd.roundToInt()} TL olur.",
                        style = MaterialTheme.typography.bodySmall)
                    Text("Reel maaş endeksi ~ %${realEnd.roundToInt()} seviyesinde.",
                        style = MaterialTheme.typography.bodySmall)
                    Text(feeling, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Graph
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Grafik: Sepet vs reel maaş endeksi", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    val blue = Color(0xFF1976D2)
                    val red = Color(0xFFD32F2F)
                    val gridColor = Color(0xFF334155)

                    val maxBasket = data.maxOfOrNull { it.basket } ?: 1.0
                    val minBasket = data.minOfOrNull { it.basket } ?: 0.0
                    val basketRange = (maxBasket - minBasket).coerceAtLeast(1.0)

                    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val left = 40f
                            val right = 8f
                            val top = 8f
                            val bottom = 24f
                            val chartW = w - left - right
                            val chartH = h - top - bottom

                            // Grid lines
                            for (i in 0..4) {
                                val y = top + chartH * i / 4
                                drawLine(gridColor, Offset(left, y), Offset(w - right, y), strokeWidth = 0.5f)
                            }

                            // Basket line (blue, left axis)
                            val basketPath = Path()
                            data.forEachIndexed { idx, pt ->
                                val x = left + chartW * idx / (data.size - 1).coerceAtLeast(1)
                                val y = top + chartH - ((pt.basket - minBasket) / basketRange * chartH).toFloat()
                                if (idx == 0) basketPath.moveTo(x, y) else basketPath.lineTo(x, y)
                            }
                            drawPath(basketPath, blue, style = Stroke(width = 2f))

                            // Real income index line (red, right axis)
                            val indexPath = Path()
                            data.forEachIndexed { idx, pt ->
                                val x = left + chartW * idx / (data.size - 1).coerceAtLeast(1)
                                val y = top + chartH - (pt.realIncomeIndex / 110.0 * chartH).toFloat()
                                if (idx == 0) indexPath.moveTo(x, y) else indexPath.lineTo(x, y)
                            }
                            drawPath(indexPath, red, style = Stroke(width = 2f))

                            // Labels
                            drawContext.canvas.nativeCanvas.apply {
                                drawText("0a", left, h - 4f, android.graphics.Paint().apply {
                                    color = 0xFF94A3B8.toInt(); textSize = 18f; android.graphics.Paint.Align.LEFT
                                })
                                drawText("${months}a", w - right, h - 4f, android.graphics.Paint().apply {
                                    color = 0xFF94A3B8.toInt(); textSize = 18f; textAlign = android.graphics.Paint.Align.RIGHT
                                })
                                drawText("Sepet", w - right - 4f, top + 14f, android.graphics.Paint().apply {
                                    color = 0xFF1976D2.toInt(); textSize = 18f; textAlign = android.graphics.Paint.Align.RIGHT
                                })
                                drawText("Reel", w - right - 4f, top + 32f, android.graphics.Paint().apply {
                                    color = 0xFFD32F2F.toInt(); textSize = 18f; textAlign = android.graphics.Paint.Align.RIGHT
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

@Composable
fun SliderCard(label: String, value: Int, rangeStart: Int, rangeEnd: Int, step: Int, onChange: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text("$label: $value", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Slider(
                value = value.toFloat(),
                onValueChange = { onChange((it / step).roundToInt() * step) },
                valueRange = rangeStart.toFloat()..rangeEnd.toFloat(),
                steps = ((rangeEnd - rangeStart) / step) - 1
            )
        }
    }
}
