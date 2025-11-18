package cl.duoc.dsy1105.moodtracker.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.dsy1105.moodtracker.domain.validators.LoginValidator
import cl.duoc.dsy1105.moodtracker.domain.validators.ValidationResult
import cl.duoc.dsy1105.moodtracker.ui.theme.MoodTrackerTheme

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onLoginClick: (String, String) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailValidation by remember { mutableStateOf<ValidationResult?>(null) }
    var passwordValidation by remember { mutableStateOf<ValidationResult?>(null) }
    var shouldShake by remember { mutableStateOf(false) }

    // Shake animation
    val shakeOffset by animateFloatAsState(
        targetValue = if (shouldShake) 1f else 0f,
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(durationMillis = 50),
            repeatMode = RepeatMode.Reverse
        ),
        finishedListener = { shouldShake = false },
        label = "shake"
    )

    val offsetX = if (shouldShake) {
        (shakeOffset * 10f) * if (shakeOffset > 0.5f) 1f else -1f
    } else {
        0f
    }

    fun handleLoginClick() {
        val validation = LoginValidator.validateLogin(email, password)
        emailValidation = validation.emailValidation
        passwordValidation = validation.passwordValidation

        if (validation.isValid) {
            onLoginClick(email, password)
        } else {
            shouldShake = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .offset(x = offsetX.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Mood Tracker",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Email field
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    // Clear validation error on change
                    if (emailValidation?.isValid == false) {
                        emailValidation = null
                    }
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = emailValidation?.isValid == false,
                trailingIcon = {
                    if (emailValidation?.isValid == false) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            if (emailValidation?.isValid == false) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = emailValidation?.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    // Clear validation error on change
                    if (passwordValidation?.isValid == false) {
                        passwordValidation = null
                    }
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordValidation?.isValid == false,
                trailingIcon = {
                    if (passwordValidation?.isValid == false) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            if (passwordValidation?.isValid == false) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = passwordValidation?.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { handleLoginClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Crear cuenta")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MoodTrackerTheme {
        LoginScreen()
    }
}
