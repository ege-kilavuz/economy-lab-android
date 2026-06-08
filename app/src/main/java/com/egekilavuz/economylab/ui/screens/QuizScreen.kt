package com.egekilavuz.economylab.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.JsonCategory
import com.egekilavuz.economylab.data.JsonQuestion
import com.egekilavuz.economylab.data.ProgressManager
import com.egekilavuz.economylab.data.Sfx
import com.egekilavuz.economylab.data.SoundManager
import com.egekilavuz.economylab.data.XpManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    category: JsonCategory,
    progressManager: ProgressManager,
    xpManager: XpManager? = null,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    // Shuffle questions on first composition
    val questionOrder = remember {
        val indices = category.quiz.indices.toMutableList()
        indices.shuffle()
        indices.take(minOf(10, category.quiz.size))
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isAnswered by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val currentQ: JsonQuestion? = if (currentIndex < questionOrder.size) {
        category.quiz[questionOrder[currentIndex]]
    } else null

    fun handleAnswer(choiceIndex: Int) {
        if (isAnswered) return
        selectedAnswer = choiceIndex
        isAnswered = true
        if (choiceIndex == currentQ?.correctIndex) {
            correctCount++
            SoundManager.getInstance(context).play(Sfx.CORRECT)
        } else {
            SoundManager.getInstance(context).play(Sfx.WRONG)
        }
    }

    fun nextQuestion() {
        if (currentIndex + 1 >= questionOrder.size) {
            isFinished = true
            progressManager.markQuizCompleted(category.id)
            val perfect = correctCount == questionOrder.size
            xpManager?.addXpForAction(if (perfect) "quiz_perfect" else "quiz_complete")
        } else {
            currentIndex++
            selectedAnswer = -1
            isAnswered = false
        }
    }

    // Result screen
    if (isFinished) {
        QuizResultScreen(
            category = category,
            correctCount = correctCount,
            total = questionOrder.size,
            onBack = onBack,
            onComplete = onComplete
        )
        return
    }

    val question = currentQ ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🧪 ${category.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / questionOrder.size },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Soru ${currentIndex + 1}/${questionOrder.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            // Soru geçiş animasyonu
            AnimatedContent(
                targetState = question.id,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(150))
                },
                label = "quizContent"
            ) {
                Column {
                    Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            ) {
                Text(
                    text = question.q,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Choices
            question.choices.forEachIndexed { index, choice ->
                val isCorrect = index == question.correctIndex
                val isSelected = index == selectedAnswer

                val backgroundColor by animateColorAsState(
                    targetValue = when {
                        isAnswered && isCorrect -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        isAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                    },
                    label = "answerBg"
                )

                val borderColor = when {
                    isAnswered && isCorrect -> MaterialTheme.colorScheme.secondary
                    isAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(borderColor.copy(alpha = 0.5f))
                    ),
                    onClick = { handleAnswer(index) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${('A' + index)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isAnswered && isCorrect) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = choice,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Explanation (shown after answering)
            if (isAnswered) {
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (selectedAnswer == question.correctIndex) "✅ Doğru!" else "❌ Yanlış",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedAnswer == question.correctIndex)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = question.explain,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { nextQuestion() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (currentIndex + 1 >= questionOrder.size) "Sonuçları Gör" else "Sonraki Soru",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun QuizResultScreen(
    category: JsonCategory,
    correctCount: Int,
    total: Int,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val pct = (correctCount * 100) / maxOf(total, 1)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "✅ Test bitti · ${category.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Skor: $correctCount / $total",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "$pct%",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = when {
                    pct >= 80 -> MaterialTheme.colorScheme.secondary
                    pct >= 50 -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.error
                }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = when {
                    pct >= 80 -> "Harika! 💪"
                    pct >= 50 -> "İyi gidiyorsun 📈"
                    else -> "Tekrar dene 🔄"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Kategorilere Dön", fontWeight = FontWeight.Bold)
            }
        }
    }
}
