package com.udacity.project.app

sealed class SignUpResult {
    data object Idle : SignUpResult()
    data object Loading : SignUpResult()
    data object Success : SignUpResult()
    data class Error(val error: SignUpError) : SignUpResult()
}

sealed class SignUpError(open val message: String) {
    data object NoInternet : SignUpError("No internet connection. Check your network and try again.")
    data object Timeout : SignUpError("Request timed out. Please try again.")
    data class ServerError(val code: Int) : SignUpError("Server error ($code). Please try again later.")
    data object EmailAlreadyExists : SignUpError("An account with this email already exists.")
    data object WeakPassword : SignUpError("Password does not meet server requirements.")
    data class Unknown(override val message: String = "Something went wrong. Please try again.") : SignUpError(message)
}
