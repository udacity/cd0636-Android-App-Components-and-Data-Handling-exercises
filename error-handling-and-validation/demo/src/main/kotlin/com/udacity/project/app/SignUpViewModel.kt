package com.udacity.project.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
// import androidx.lifecycle.viewModelScope
// import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.random.Random

// import kotlinx.coroutines.launch
// import java.io.IOException
// import java.net.SocketTimeoutException
// import kotlin.random.Random

class SignUpViewModel : ViewModel() {

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    private val _signUpState = MutableStateFlow<SignUpResult>(SignUpResult.Idle)
    val signUpState: StateFlow<SignUpResult> = _signUpState.asStateFlow()

    private var currentEmail = ""
    private var currentPassword = ""
    private var currentConfirmPassword = ""

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
        _isFormValid.value = _emailError.value == null && currentEmail.isNotBlank()
                && _passwordError.value == null && currentPassword.isNotBlank()
                && _confirmPasswordError.value == null && currentConfirmPassword.isNotBlank()
    }

    // TODO Part 2: Implement signUp() with try/catch error handling
    //  1. Validate all fields first. If any fail, return early.
    //  2. Set _signUpState to Loading
    //  3. Wrap the network call in try/catch, mapping each exception to a SignUpError
    //  Hint: Use viewModelScope.launch { ... }
    fun signUp() {
        val emailResult = validateEmail(currentEmail)
        val passwordResult = validatePassword(currentPassword)
        val confirmResult = validateConfirmPassword(currentConfirmPassword)

        if (emailResult is ValidationResult.Invalid
            || passwordResult is ValidationResult.Invalid
            || confirmResult is ValidationResult.Invalid
        ) {
            return
        }

        viewModelScope.launch {
            try {
                _signUpState.value = SignUpResult.Loading

                delay(1500)

                when (Random.nextInt(7)) {
                    0 -> throw IOException("No internet connection")
                    1 -> throw SocketTimeoutException("Request timed out")
                    2 -> throw HttpException(500)
                    3 -> throw EmailAlreadyExistsException()
                    4 -> throw WeakPasswordException()
                    5 -> throw Exception("Something went wrong")
                }

                _signUpState.value = SignUpResult.Success

            } catch (e: IOException) {
                _signUpState.value = SignUpResult.Error(SignUpError.NoInternet)
            } catch (e: SocketTimeoutException) {
                _signUpState.value = SignUpResult.Error(SignUpError.Timeout)
            } catch (e: HttpException) {
                _signUpState.value = SignUpResult.Error(SignUpError.ServerError(e.code))
            } catch (e: EmailAlreadyExistsException) {
                _signUpState.value = SignUpResult.Error(SignUpError.EmailAlreadyExists)
            } catch (e: WeakPasswordException) {
                _signUpState.value = SignUpResult.Error(SignUpError.WeakPassword)
            } catch (e: Exception) {
                _signUpState.value =
                    SignUpResult.Error(SignUpError.Unknown(e.message ?: "Unknown error"))
            }
        }
    }
}

class HttpException(val code: Int) : Exception("HTTP $code")
class EmailAlreadyExistsException : Exception("Email already exists")
class WeakPasswordException : Exception("Weak password")
