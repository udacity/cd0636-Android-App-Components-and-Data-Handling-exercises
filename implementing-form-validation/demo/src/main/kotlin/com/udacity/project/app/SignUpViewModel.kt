package com.udacity.project.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    private var currentEmail = ""
    private var currentPassword = ""
    private var currentConfirmPassword = ""

    // Part 1.1: Implement email validation
    fun validateEmail(email: String): ValidationResult {
        currentEmail = email
        val result = when {
            email.isBlank() -> ValidationResult.Invalid("Email is required")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult.Invalid("Enter a valid email address")
            else -> ValidationResult.Valid
        }
        _emailError.value = (result as? ValidationResult.Invalid)?.message
        updateFormValidity()
        return result
    }

    // Part 1.2: Implement password validation
    fun validatePassword(password: String): ValidationResult {
        currentPassword = password
        val result = when {
            password.isBlank() -> ValidationResult.Invalid("Password is required")
            password.length < 8 -> ValidationResult.Invalid("Password must be at least 8 characters")
            !password.any { it.isUpperCase() } ->
                ValidationResult.Invalid("Password must contain an uppercase letter")
            !password.any { it.isDigit() } ->
                ValidationResult.Invalid("Password must contain a number")
            else -> ValidationResult.Valid
        }
        _passwordError.value = (result as? ValidationResult.Invalid)?.message
        if (currentConfirmPassword.isNotEmpty()) {
            validateConfirmPassword(currentConfirmPassword)
        }
        updateFormValidity()
        return result
    }

    // Part 1.3: Implement confirm-password validation
    fun validateConfirmPassword(confirmPassword: String): ValidationResult {
        currentConfirmPassword = confirmPassword
        val result = when {
            confirmPassword.isBlank() -> ValidationResult.Invalid("Please confirm your password")
            confirmPassword != currentPassword ->
                ValidationResult.Invalid("Passwords do not match")
            else -> ValidationResult.Valid
        }
        _confirmPasswordError.value = (result as? ValidationResult.Invalid)?.message
        updateFormValidity()
        return result
    }

    private fun updateFormValidity() {
        val isEmailValid = _emailError.value == null && currentEmail.isNotBlank()
        val isPasswordValid = _passwordError.value == null && currentPassword.isNotBlank()
        val isConfirmPasswordValid =
            _confirmPasswordError.value == null && currentConfirmPassword.isNotBlank()

        _isFormValid.value = isEmailValid && isPasswordValid && isConfirmPasswordValid
    }

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    fun signUp() {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            delay(2000) // Simulate network call
            _signUpState.value = SignUpState.Success
        }
    }

    sealed class SignUpState {
        data object Idle : SignUpState()
        data object Loading : SignUpState()
        data object Success : SignUpState()
    }
}
