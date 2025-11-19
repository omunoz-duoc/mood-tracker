package cl.duoc.dsy1105.moodtracker.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.dsy1105.moodtracker.domain.validators.RegisterValidator
import cl.duoc.dsy1105.moodtracker.domain.validators.ValidationResult
import cl.duoc.dsy1105.moodtracker.ui.theme.MoodTrackerTheme
import cl.duoc.dsy1105.moodtracker.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit = {},
    onRegistrationSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: RegisterViewModel = viewModel { RegisterViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailValidation by remember { mutableStateOf<ValidationResult?>(null) }
    var passwordValidation by remember { mutableStateOf<ValidationResult?>(null) }
    var confirmPasswordValidation by remember { mutableStateOf<ValidationResult?>(null) }
    var shouldShake by remember { mutableStateOf(false) }

    // Handle registration success
    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            onRegistrationSuccess()
            viewModel.resetRegistrationSuccess()
        }
    }

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

    fun handleRegisterClick() {
        val validation = RegisterValidator.validateRegister(email, password, confirmPassword)
        emailValidation = validation.emailValidation
        passwordValidation = validation.passwordValidation
        confirmPasswordValidation = validation.confirmPasswordValidation

        if (validation.isValid) {
            viewModel.register(email, password, confirmPassword)
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
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Show error message from ViewModel
        if (uiState.errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Email field
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailValidation?.isValid == false) {
                        emailValidation = null
                    }
                    if (uiState.errorMessage != null) {
                        viewModel.clearError()
                    }
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isLoading,
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
                Text(
                    text = emailValidation?.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordValidation?.isValid == false) {
                        passwordValidation = null
                    }
                    if (confirmPasswordValidation?.isValid == false) {
                        confirmPasswordValidation = null
                    }
                    if (uiState.errorMessage != null) {
                        viewModel.clearError()
                    }
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                enabled = !uiState.isLoading,
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
                Text(
                    text = passwordValidation?.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password field
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    if (confirmPasswordValidation?.isValid == false) {
                        confirmPasswordValidation = null
                    }
                    if (uiState.errorMessage != null) {
                        viewModel.clearError()
                    }
                },
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                enabled = !uiState.isLoading,
                isError = confirmPasswordValidation?.isValid == false,
                trailingIcon = {
                    if (confirmPasswordValidation?.isValid == false) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            if (confirmPasswordValidation?.isValid == false) {
                Text(
                    text = confirmPasswordValidation?.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { handleRegisterClick() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateBack,
            enabled = !uiState.isLoading
        ) {
            Text("Ya tengo cuenta - Iniciar sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MoodTrackerTheme {
        RegisterScreen()
    }
}
