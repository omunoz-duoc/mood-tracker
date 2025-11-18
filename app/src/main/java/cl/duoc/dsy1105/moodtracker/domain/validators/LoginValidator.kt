package cl.duoc.dsy1105.moodtracker.domain.validators

import android.util.Patterns

object LoginValidator {

    private const val MIN_PASSWORD_LENGTH = 6

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(
                isValid = false,
                errorMessage = "El email no puede estar vacío"
            )
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult(
                isValid = false,
                errorMessage = "El formato del email no es válido"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(
                isValid = false,
                errorMessage = "La contraseña no puede estar vacía"
            )
            password.length < MIN_PASSWORD_LENGTH -> ValidationResult(
                isValid = false,
                errorMessage = "La contraseña debe tener al menos $MIN_PASSWORD_LENGTH caracteres"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    fun validateLogin(email: String, password: String): LoginValidationResult {
        return LoginValidationResult(
            emailValidation = validateEmail(email),
            passwordValidation = validatePassword(password)
        )
    }
}
