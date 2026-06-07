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
import kotlin.math.roundToInt

private data class CBPoint(val m: Int, val inflation: Double, val unemployment: Double, val growth: Double)

private fun simulateCB(policyRate: Int, supplyShock: Int, demand: Int): List<CBPoint> {
    val points = mutableListOf<CBPoint>()
    var inflation = 35.0 + supplyShock
    var unemployment = 10.0
    var growth = 3.0 + demand * 0.2
    val rateTightness = (policyRate - 30) / 50.0

    for (m in 0..12) {
        val infChange = -0.8 * rateTightness + 0.15 * supplyShock - 0.1 * demand
        val unempChange = 0.25 * rateTightness - 0.05 * demand
        val growthChange = -0.35 * rateTightness + 0.08 * demand
        inflation += infChange; unemployment += unempChange; growth += growthChange
        points.add(CBPoint(m, inflation.coerceAtLeast(0.0), unemployment.coerceAtLeast(0.0), growth.coerceAtLeast(-5.0)))
    }
    return points
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentralBankSimulation(onBack: () -> Unit) {
    var policyRate by remember { mutableIntStateOf(45) }
    var supplyShock by remember { mutableIntStateOf(0) }
    var demand by remember { mutableIntStateOf(0) }

    val data = remember(policyRate, supplyShock, demand) {
        simulateCB(policyRate, supplyShock, demand)
    }

    val last = data.last()

    Scaffold(topBar = {
        TopAppBar(title = { Text("🏛️ MB Karar Odası", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 12.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            Text("Faiz, enflasyon, işsizlik ve büyüme arasındaki dengeyi gözlemle.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            SliderCard("Politika faizi (%)", policyRate, 5, 80, 1) { policyRate = it }
            SliderCard("Arz şoku", supplyShock, -10, 20, 1) { supplyShock = it }
            SliderCard("Talep ısısı", demand, -10, 10, 1) { demand = it }

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(Modifier.padding(14.dp)) {
                    Text("12. Ay Sonuçları", fontWeight = FontWeight.Bold)
                    Text("Enflasyon: %${last.inflation.roundToInt()}")
                    Text("İşsizlik: %${last.unemployment.roundToInt()}")
                    Text("Büyüme: %${last.growth.roundToInt()}")
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
                            val allValues = data.flatMap { listOf(it.inflation, it.unemployment, (it.growth + 5).coerceAtLeast(0.0)) }
                            val maxV = allValues.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0

                            fun drawLine(getY: (CBPoint) -> Double, color: Color) {
                                val path = Path()
                                data.forEachIndexed { i, p ->
                                    val x = left + cw * i / data.size.coerceAtLeast(1)
                                    val y = top + ch - (getY(p) / maxV * ch).toFloat()
                                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                }
                                drawPath(path, color, style = Stroke(width = 2f))
                            }
                            drawLine({ it.inflation }, Color(0xFFEF4444)) // red
                            drawLine({ it.unemployment }, Color(0xFFF59E0B)) // yellow
                            drawLine({ (it.growth + 5).coerceAtLeast(0.0) }, Color(0xFF22C55E)) // green

                            drawContext.canvas.nativeCanvas.apply {
                                drawText("0", left, h - 4f, android.graphics.Paint().apply {
                                    color = 0xFF94A3B8.toInt(); textSize = 14f; textAlign = android.graphics.Paint.Align.LEFT
                                })
                                drawText("12a", w - right, h - 4f, android.graphics.Paint().apply {
                                    color = 0xFF94A3B8.toInt(); textSize = 14f; textAlign = android.graphics.Paint.Align.RIGHT
                                })
                                drawText("Enf", left + 4f, top + 14f, android.graphics.Paint().apply {
                                    color = 0xFFEF4444.toInt(); textSize = 14f; textAlign = android.graphics.Paint.Align.LEFT
                                })
                                drawText("İşs", left + 4f, top + 30f, android.graphics.Paint().apply {
                                    color = 0xFFF59E0B.toInt(); textSize = 14f; textAlign = android.graphics.Paint.Align.LEFT
                                })
                                drawText("Büy", left + 4f, top + 46f, android.graphics.Paint().apply {
                                    color = 0xFF22C55E.toInt(); textSize = 14f; textAlign = android.graphics.Paint.Align.LEFT
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
