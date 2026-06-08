package com.egekilavuz.economylab.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.JsonCategory
import com.egekilavuz.economylab.data.JsonContent

@Composable
fun HomeScreen(
    content: JsonContent,
    onCategoryClick: (String) -> Unit,
    onDailyQuizClick: (() -> Unit)? = null,
    onStatsClick: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCategories = remember(searchQuery, content) {
        if (searchQuery.isBlank()) {
            if (content.learningPath.isNotEmpty()) {
                val orderMap = content.learningPath.withIndex().associate { (i, id) -> id to i }
                content.categories.sortedBy { orderMap[it.id] ?: Int.MAX_VALUE }
            } else content.categories
        } else {
            val q = searchQuery.lowercase()
            content.categories.filter {
                it.title.lowercase().contains(q) ||
                it.subtitle.lowercase().contains(q) ||
                it.items.any { item -> item.title.lowercase().contains(q) }
            }.sortedBy { it.title }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "📚 Öğren",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            if (onStatsClick != null) {
                FilledTonalButton(
                    onClick = onStatsClick,
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("📊", fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("İstatistik", fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = "Finansal okuryazarlık için 12 konu başlığı",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Kategori ara...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Temizle")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )

        Spacer(Modifier.height(8.dp))

        // Daily Quiz banner
        if (onDailyQuizClick != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).clickable(onClick = onDailyQuizClick),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("🌟 Günlük Quiz", fontWeight = FontWeight.Bold)
                        Text("Her gün 5 yeni soru · XP kazan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    SuggestionChip(
                        onClick = onDailyQuizClick,
                        label = { Text("BAŞLA", fontWeight = FontWeight.Bold) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (filteredCategories.isEmpty() && searchQuery.isNotBlank()) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "\"$searchQuery\" için sonuç bulunamadı",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 1000.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(filteredCategories) { cat ->
                    CategoryCard(
                        icon = cat.icon,
                        title = cat.title,
                        subtitle = cat.subtitle,
                        onClick = { onCategoryClick(cat.id) }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun CategoryCard(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(3 / 2f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .animateContentSize(animationSpec = spring(dampingRatio = 0.6f))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 22.sp, modifier = Modifier.padding(bottom = 4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
