package cl.duoc.dsy1105.moodtracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.dsy1105.moodtracker.data.local.entities.MoodEntry
import cl.duoc.dsy1105.moodtracker.domain.model.MoodType
import cl.duoc.dsy1105.moodtracker.ui.theme.MoodTrackerTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    moodEntries: List<MoodEntry> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Emociones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                moodEntries.isEmpty() -> {
                    EmptyHistoryState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    MoodHistoryList(
                        moodEntries = moodEntries,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun MoodHistoryList(
    moodEntries: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = moodEntries,
            key = { _, entry -> entry.id }
        ) { index, entry ->
            var isVisible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(index * 50L)
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                MoodHistoryCard(moodEntry = entry)
            }
        }
    }
}

@Composable
fun MoodHistoryCard(
    moodEntry: MoodEntry,
    modifier: Modifier = Modifier
) {
    val moodType = moodEntry.toMoodType()
    val dateFormat = SimpleDateFormat("d 'de' MMMM, yyyy · HH:mm", Locale("es", "ES"))
    val formattedDate = dateFormat.format(Date(moodEntry.date))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mood emoji with colored background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color(moodType.color).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = moodType.emoji,
                    fontSize = 32.sp
                )
            }

            // Mood details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = moodType.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                if (!moodEntry.note.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = moodEntry.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📊",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay registros aún",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Empieza registrando tu estado de ánimo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    MoodTrackerTheme {
        val sampleEntries = listOf(
            MoodEntry(
                id = 1,
                userId = 1,
                moodType = MoodType.HAPPY.name,
                note = "Tuve un excelente día en el trabajo",
                date = System.currentTimeMillis()
            ),
            MoodEntry(
                id = 2,
                userId = 1,
                moodType = MoodType.CALM.name,
                note = "Meditación matutina",
                date = System.currentTimeMillis() - 86400000
            ),
            MoodEntry(
                id = 3,
                userId = 1,
                moodType = MoodType.ANXIOUS.name,
                note = null,
                date = System.currentTimeMillis() - 172800000
            )
        )
        HistoryScreen(moodEntries = sampleEntries)
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyHistoryScreenPreview() {
    MoodTrackerTheme {
        HistoryScreen(moodEntries = emptyList())
    }
}
