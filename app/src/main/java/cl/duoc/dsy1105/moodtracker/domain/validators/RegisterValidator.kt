package cl.duoc.dsy1105.moodtracker.domain.validators

import android.util.Patterns

data class RegisterValidationResult(
    val emailValidation: ValidationResult,
    val passwordValidation: ValidationResult,
    val confirmPasswordValidation: ValidationResult
) {
    val isValid: Boolean
        get() = emailValidation.isValid &&
                passwordValidation.isValid &&
                confirmPasswordValidation.isValid
}

object RegisterValidator {

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

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult(
                isValid = false,
                errorMessage = "Debes confirmar la contraseña"
            )
            password != confirmPassword -> ValidationResult(
                isValid = false,
                errorMessage = "Las contraseñas no coinciden"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    fun validateRegister(email: String, password: String, confirmPassword: String): RegisterValidationResult {
        return RegisterValidationResult(
            emailValidation = validateEmail(email),
            passwordValidation = validatePassword(password),
            confirmPasswordValidation = validateConfirmPassword(password, confirmPassword)
        )
    }
}
