package com.egekilavuz.economylab.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.GameViewModel

data class DailySnapshot(
    val day: Int,
    val cash: Int,
    val mood: Int,
    val fridge: Int,
    val discipline: Int,
    val cardDebt: Int
)

@Composable
fun GameTrendChart(
    history: List<DailySnapshot>,
    modifier: Modifier = Modifier
) {
    if (history.size < 2) return

    val cashColor = Color(0xFF4CAF50)
    val moodColor = Color(0xFF2196F3)
    val fridgeColor = Color(0xFFFF9800)
    val debtColor = Color(0xFFF44336)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("📈 Günlük Trend", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LegendDot(cashColor, "Nakit")
                LegendDot(moodColor, "Moral")
                LegendDot(fridgeColor, "Dolap")
                LegendDot(debtColor, "Borç")
            }

            Spacer(Modifier.height(8.dp))

            // Chart
            Canvas(
                modifier = Modifier.fillMaxWidth().height(160.dp)
            ) {
                val w = size.width
                val h = size.height
                val padding = 30f
                val chartW = w - padding * 2
                val chartH = h - padding * 2

                // Find min/max
                val allValues = history.flatMap {
                    listOf(it.cash.toFloat(), it.mood.toFloat(), it.fridge.toFloat(), it.cardDebt.toFloat())
                }
                val minVal = (allValues.minOrNull() ?: 0f).coerceAtMost(0f)
                val maxVal = (allValues.maxOrNull() ?: 1f).coerceAtLeast(1f)
                val range = (maxVal - minVal).coerceAtLeast(1f)

                fun normalize(value: Float): Float =
                    padding + chartH - ((value - minVal) / range * chartH)

                fun drawLine(values: List<Float>, color: Color) {
                    if (values.size < 2) return
                    val path = Path()
                    values.forEachIndexed { i, v ->
                        val x = padding + (i.toFloat() / (values.size - 1).coerceAtLeast(1)) * chartW
                        val y = normalize(v)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(
                        path,
                        color = color,
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                drawLine(history.map { it.cash.toFloat() }, cashColor)
                drawLine(history.map { it.mood.toFloat() }, moodColor)
                drawLine(history.map { it.fridge.toFloat() }, fridgeColor)
                drawLine(history.map { it.cardDebt.toFloat() }, debtColor)

                // Day labels (start, middle, end)
                val dayLabels = listOf(0, history.size / 2, history.size - 1)
                dayLabels.forEach { i ->
                    val x = padding + (i.toFloat() / (history.size - 1).coerceAtLeast(1)) * chartW
                    drawLine(
                        start = Offset(x, padding),
                        end = Offset(x, padding + chartH),
                        color = Color.Gray.copy(alpha = 0.2f),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(color = color)
        }
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
    }
}
