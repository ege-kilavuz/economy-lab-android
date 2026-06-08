package com.egekilavuz.economylab.ui.screens

import androidx.compose.animation.animateColorAsState
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
import com.egekilavuz.economylab.data.ContentLoader
import com.egekilavuz.economylab.data.ProgressManager
import com.egekilavuz.economylab.data.XpManager
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyQuizScreen(
    contentLoader: ContentLoader,
    xpManager: XpManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val content = remember { contentLoader.load(context) }
    val progressManager = remember { ProgressManager(context) }

    val monthKey = remember { xpManager.getCurrentMonthKey() }
    val todayDay = remember { xpManager.getTodayDayOfMonth() }
    val usedIds = remember { xpManager.getUsedDailyQuestionIds() }

    // Get the monthly pool, pick today's 5 unique questions
    val monthPool = remember {
        content.dailyQuiz[monthKey] ?: emptyList()
    }

    // Today's questions: skip used ones, pick 5 for today based on day seed
    val questions = remember {
        val daySeed = monthKey.hashCode() * 31 + todayDay
        val rng = Random(daySeed)
        val available = monthPool.filter { it.id !in usedIds || true } // ignore used on first load
        val shuffled = if (available.size >= 5) available.shuffled(rng) else monthPool.shuffled(rng)
        shuffled.take(5)
    }

    var started by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isAnswered by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var xpEarned by remember { mutableIntStateOf(0) }

    val alreadyDone = remember { xpManager.isDailyQuizDone() }

    val currentQ = if (currentIndex < questions.size) questions[currentIndex] else null

    fun handleAnswer(choiceIndex: Int) {
        if (isAnswered) return
        selectedAnswer = choiceIndex
        isAnswered = true
        if (choiceIndex == currentQ?.correctIndex) {
            correctCount++
        }
    }

    fun nextQuestion() {
        if (currentIndex + 1 >= questions.size) {
            finished = true
            // Mark all today's questions as used so they won't repeat this month
            questions.forEach { q ->
                xpManager.markDailyQuestionUsed(q.id)
            }
            val xp = if (correctCount == questions.size) XpManager.XP_DAILY_QUIZ + 15 else XpManager.XP_DAILY_QUIZ
            xpEarned = xp
            xpManager.addXp(xp)
            xpManager.markDailyQuizDone()
            progressManager.markDailyQuizCompleted()
        } else {
            currentIndex++
            selectedAnswer = -1
            isAnswered = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🌟 Günlük Quiz",
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
            Spacer(Modifier.height(8.dp))

            // Info card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                )
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("🌟 Günlük Quiz", fontWeight = FontWeight.Bold)
                    Text("Her gün 5 rastgele soru. Tümünü doğru bil + bonus!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(12.dp))

            if (alreadyDone && !finished) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    )
                ) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✅ Bugünkü quiz tamamlandı!", fontWeight = FontWeight.Bold)
                        Text("Yarın yeni sorularla tekrar gel.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else if (finished) {
                // Result
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                ) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "✅ Günlük Quiz Bitti!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "$correctCount / ${questions.size}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = when {
                                correctCount == questions.size -> MaterialTheme.colorScheme.secondary
                                correctCount >= 3 -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.error
                            }
                        )
                        if (correctCount == questions.size) {
                            Text("Mükemmel! 🎉", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("+$xpEarned XP kazandın!", fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onBack, modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)) {
                            Text("Ana Sayfaya Dön")
                        }
                    }
                }
            } else if (!started) {
                Button(
                    onClick = { started = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Başla", fontWeight = FontWeight.Bold)
                }
            } else if (currentQ != null) {
                val question = currentQ

                // Progress
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / questions.size },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text("Soru ${currentIndex + 1}/${questions.size} · Günlük",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(16.dp))

                // Question card
                Card(
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

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        onClick = { handleAnswer(index) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${('A' + index)}", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(12.dp))
                            Text(choice, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                // Explanation
                if (isAnswered) {
                    Spacer(Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                text = if (selectedAnswer == question.correctIndex) "✅ Doğru!" else "❌ Yanlış",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedAnswer == question.correctIndex)
                                    MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                            )
                            Text(question.explain, style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { nextQuestion() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (currentIndex + 1 >= questions.size) "Sonuçları Gör" else "Sonraki Soru",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
