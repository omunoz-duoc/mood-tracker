package cl.duoc.dsy1105.moodtracker.domain.validators

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

data class LoginValidationResult(
    val emailValidation: ValidationResult,
    val passwordValidation: ValidationResult
) {
    val isValid: Boolean
        get() = emailValidation.isValid && passwordValidation.isValid
}
