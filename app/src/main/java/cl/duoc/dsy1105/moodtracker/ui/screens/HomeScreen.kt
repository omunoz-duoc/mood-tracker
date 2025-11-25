package cl.duoc.dsy1105.moodtracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.dsy1105.moodtracker.R
import cl.duoc.dsy1105.moodtracker.ui.theme.MoodTrackerTheme
import java.text.SimpleDateFormat
import java.util.*

// Data class for mood entries
data class MoodEntry(
    val moodType: String,
    val moodEmoji: String,
    val date: Date,
    val tags: List<String>,
    val note: String,
    val hasAudio: Boolean = false,
    val imageCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userEmail: String = "",
    notificationsEnabled: Boolean = false,
    onLogout: () -> Unit = {},
    onTrackMood: () -> Unit = {},
    onViewHistory: () -> Unit = {},
    onToggleNotifications: (Boolean) -> Unit = {},
    onMoodSelected: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onAddDetails: () -> Unit = {}
) {
    val sampleMoodEntries = listOf(
        MoodEntry(
            moodType = "Excelente",
            moodEmoji = "😄",
            date = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000),
            tags = listOf("💼 Trabajo", "👪 Familia", "💪 Ejercicio"),
            note = "Tuve un día increíble hoy! Completé mi proyecto y salí a correr por la tarde.",
            hasAudio = true,
            imageCount = 2
        ),
        MoodEntry(
            moodType = "Bien",
            moodEmoji = "🙂",
            date = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000),
            tags = listOf("👥 Social", "😌 Relax"),
            note = "Día tranquilo con amigos. Vimos una película y charlamos mucho.",
            hasAudio = false,
            imageCount = 1
        ),
        MoodEntry(
            moodType = "Meh",
            moodEmoji = "😐",
            date = Date(System.currentTimeMillis() - 48 * 60 * 60 * 1000),
            tags = listOf("💼 Trabajo", "😰 Estrés"),
            note = "Día normal, nada especial. Un poco cansado del trabajo.",
            hasAudio = false,
            imageCount = 0
        )
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Inicio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Image(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Mood Selection Section
            MoodSelectionSection(
                onMoodSelected = onMoodSelected,
                onAddDetails = onAddDetails
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Moods Section
            Text(
                text = "Estados de ánimo recientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mood Entry Cards
            sampleMoodEntries.forEach { entry ->
                MoodEntryCard(entry = entry)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun MoodSelectionSection(
    onMoodSelected: (String) -> Unit,
    onAddDetails: () -> Unit = {}
) {
    val moods = listOf(
        "😄" to "Excelente",
        "🙂" to "Bien",
        "😐" to "Meh",
        "😟" to "Mal",
        "😢" to "Pésimo"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Cómo te sientes hoy?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                moods.forEach { (emoji, label) ->
                    MoodOption(
                        emoji = emoji,
                        label = "",
                        onClick = { onMoodSelected(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onAddDetails,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Agregar más detalles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun MoodOption(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.displayMedium
        )
        if (label.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MoodEntryCard(entry: MoodEntry) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("es", "ES"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Mood icon, text, date, and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = entry.moodEmoji,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Column {
                        Text(
                            text = entry.moodType,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = dateFormat.format(entry.date),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { /* More options */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Más opciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tags
            if (entry.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entry.tags) { tag ->
                        AssistChip(
                            onClick = { },
                            label = { Text(tag) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.White
                            ),
                            shape = CircleShape
                        )
                    }
                }
            }

            // Note text
            if (entry.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = entry.note,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Audio attachment
            if (entry.hasAudio) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Reproducir audio",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Nota de voz",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "0:24",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Image attachments
            if (entry.imageCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(entry.imageCount) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = Color.White,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "📷",
                                    style = MaterialTheme.typography.headlineLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MoodTrackerTheme {
        HomeScreen(userEmail = "usuario@ejemplo.com")
    }
}
