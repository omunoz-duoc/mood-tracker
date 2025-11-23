package cl.duoc.dsy1105.moodtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.dsy1105.moodtracker.ui.theme.LoadingGreen
import cl.duoc.dsy1105.moodtracker.ui.theme.MoodTrackerTheme
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onNavigateToNext: () -> Unit
) {
    // Navigate after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onNavigateToNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LoadingGreen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo placeholder (will be replaced with actual logo)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White, shape = androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for logo image
                Text(
                    text = "🎭",
                    fontSize = 60.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App name
            Text(
                text = "Muud",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Loading spinner
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color.White,
                strokeWidth = 4.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    MoodTrackerTheme {
        LoadingScreen(onNavigateToNext = {})
    }
}
