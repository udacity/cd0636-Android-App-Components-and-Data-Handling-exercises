package com.udacity.project.app

sealed class AuthState {
    data class Authenticated(val userId: String, val email: String) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
