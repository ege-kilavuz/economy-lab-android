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

private data class BESPoint(val y: Int, val personal: Double, val govt: Double, val total: Double)

private fun simulateBES(monthly: Int, years: Int, returnRate: Int, govtMatch: Int): List<BESPoint> {
    val monthlyRate = returnRate / 100.0 / 12.0
    val points = mutableListOf<BESPoint>()
    var personalTotal = 0.0
    var govtTotal = 0.0

    for (m in 0..years * 12) {
        if (m > 0) {
            val contribution = monthly.toDouble()
            val govtContribution = monthly * (govtMatch / 100.0)
            personalTotal = (personalTotal + contribution) * (1 + monthlyRate)
            govtTotal = (govtTotal + govtContribution) * (1 + monthlyRate)
        }
        if (m % 12 == 0) {
            points.add(BESPoint(m / 12, personalTotal, govtTotal, personalTotal + govtTotal))
        }
    }
    return points
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BESSimulation(onBack: () -> Unit) {
    var monthly by remember { mutableIntStateOf(2000) }
    var years by remember { mutableIntStateOf(10) }
    var returnRate by remember { mutableIntStateOf(15) }
    var govtMatch by remember { mutableIntStateOf(30) }

    val data = remember(monthly, years, returnRate, govtMatch) {
        simulateBES(monthly, years, returnRate, govtMatch)
    }
    val last = data.lastOrNull()

    Scaffold(topBar = {
        TopAppBar(title = { Text("💰 BES", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 12.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            Text("Devlet katkısı %30 — erken başlayan biriktirir.", style = MaterialTheme.typography.bodySmall)
            SliderCard("Aylık katkı (TL)", monthly, 500, 20000, 250) { monthly = it }
            SliderCard("Vade (yıl)", years, 3, 30, 1) { years = it }
            SliderCard("Yıllık getiri (%)", returnRate, 5, 40, 1) { returnRate = it }
            SliderCard("Devlet katkısı (%)", govtMatch, 0, 50, 5) { govtMatch = it }

            if (last != null) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Sonuç", fontWeight = FontWeight.Bold)
                        Text("Toplam birikim: ${last.total.roundToInt()} TL")
                        Text("Kendi katkın: ${last.personal.roundToInt()} TL")
                        Text("Devlet katkısı: ${last.govt.roundToInt()} TL")
                        Text("Devlet katkısı oranı: %${(last.govt / last.personal * 100).roundToInt()}")
                    }
                }
            }

            // 3-line chart
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(Modifier.padding(14.dp)) {
                    Text("Grafik", fontWeight = FontWeight.Bold)
                    Box(Modifier.fillMaxWidth().height(180.dp)) {
                        Canvas(Modifier.fillMaxSize()) {
                            val w = size.width; val h = size.height
                            val left = 36f; val right = 8f; val top = 8f; val bottom = 24f
                            val cw = w - left - right; val ch = h - top - bottom
                            val maxV = data.maxOfOrNull { it.total }?.coerceAtLeast(1.0) ?: 1.0

                            fun drawLine(getY: (BESPoint) -> Double, color: Color) {
                                val path = Path()
                                data.forEachIndexed { i, p ->
                                    val x = left + cw * i / (data.size - 1).coerceAtLeast(1)
                                    val y = top + ch - (getY(p) / maxV * ch).toFloat()
                                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                }
                                drawPath(path, color, style = Stroke(width = 2f))
                            }
                            drawLine({ it.total }, Color(0xFF22C55E))
                            drawLine({ it.personal }, Color(0xFF1976D2))
                            drawLine({ it.govt }, Color(0xFFF59E0B))

                            drawContext.canvas.nativeCanvas.apply {
                                drawText("0", left, h - 4f, android.graphics.Paint().apply {
                                    color = 0xFF94A3B8.toInt(); textSize = 14f; textAlign = android.graphics.Paint.Align.LEFT
                                })
                                drawText("${years}y", w - right, h - 4f, android.graphics.Paint().apply {
                                    color = 0xFF94A3B8.toInt(); textSize = 14f; textAlign = android.graphics.Paint.Align.RIGHT
                                })
                                drawText("Toplam", left + 4f, top + 14f, android.graphics.Paint().apply {
                                    color = 0xFF22C55E.toInt(); textSize = 14f; textAlign = android.graphics.Paint.Align.LEFT
                                })
                                drawText("Kendi", left + 4f, top + 30f, android.graphics.Paint().apply {
                                    color = 0xFF1976D2.toInt(); textSize = 14f; textAlign = android.graphics.Paint.Align.LEFT
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
