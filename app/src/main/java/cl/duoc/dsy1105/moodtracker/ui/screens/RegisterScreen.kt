package cl.duoc.dsy1105.moodtracker.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.dsy1105.moodtracker.R
import cl.duoc.dsy1105.moodtracker.domain.validators.RegisterValidator
import cl.duoc.dsy1105.moodtracker.domain.validators.ValidationResult
import cl.duoc.dsy1105.moodtracker.ui.theme.MoodTrackerTheme
import cl.duoc.dsy1105.moodtracker.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onRegistrationSuccess: () -> Unit = {},
    onSocialRegister: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: RegisterViewModel = viewModel { RegisterViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
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
            .offset(x = offsetX.dp)
    ) {
        // Back arrow
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Title with sparkle emoji
            Text(
                text = "Únete a Muud Hoy ✨",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Empieza a registrar tus estados de ánimo y gana insignias de logros.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

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

            // Email label
            Text(
                text = "Email",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email field
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
                placeholder = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email icon"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = emailValidation?.isValid == false,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            if (emailValidation?.isValid == false) {
                Text(
                    text = emailValidation?.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password label
            Text(
                text = "Contraseña",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    confirmPassword = it
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
                placeholder = { Text("Contraseña") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Password icon"
                    )
                },
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "Ocultar" else "Mostrar",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                enabled = !uiState.isLoading,
                isError = passwordValidation?.isValid == false,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            if (passwordValidation?.isValid == false) {
                Text(
                    text = passwordValidation?.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = buildAnnotatedString {
                        append("Acepto los ")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("Términos y Condiciones")
                        }
                        append(" de Muud.")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Already have account
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ya tienes una cuenta? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Ingresa",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Or continue with
            Text(
                text = "o continúa con",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Social login icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { onSocialRegister("google") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Unspecified
                    )
                }
                IconButton(onClick = { onSocialRegister("apple") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.apple),
                        contentDescription = "Apple",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Unspecified
                    )
                }
                IconButton(onClick = { onSocialRegister("facebook") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.facebook),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Unspecified
                    )
                }
                IconButton(onClick = { onSocialRegister("x") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.x),
                        contentDescription = "X",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign up button
            Button(
                onClick = { handleRegisterClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading && agreedToTerms,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Registrarse",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
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
