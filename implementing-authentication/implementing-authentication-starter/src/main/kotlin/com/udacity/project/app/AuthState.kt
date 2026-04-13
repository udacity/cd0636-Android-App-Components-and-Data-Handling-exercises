package com.udacity.project.app

/**
 * Sealed class representing the authentication state of the user.
 * This provides a type-safe way to represent different auth states.
 */
sealed class AuthState {
    /**
     * User is authenticated with valid credentials.
     * @param userId The unique Firebase user ID
     * @param email The user's email address
     * @param token The Firebase ID token (optional, retrieved asynchronously)
     */
    data class Authenticated(
        val userId: String,
        val email: String,
        val token: String? = null
    ) : AuthState()

    /**
     * User is not authenticated (logged out).
     */
    object Unauthenticated : AuthState()

    /**
     * Authentication operation is in progress.
     */
    object Loading : AuthState()

    /**
     * Authentication error occurred.
     * @param message Error message to display to user
     */
    data class Error(val message: String) : AuthState()

    /**
     * User session has expired and needs to re-authenticate.
     */
    object SessionExpired : AuthState()
}