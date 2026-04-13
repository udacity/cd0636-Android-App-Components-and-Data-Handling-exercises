package com.udacity.project.app

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

/**
 * ViewModel for managing authentication state and operations.
 * Handles login, sign-up, logout, and session management with Firebase Auth.
 */
class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _authState = MutableLiveData<AuthState>(AuthState.Unauthenticated)
    val authState: LiveData<AuthState> = _authState

    /**
     * TODO 3.2: Firebase Auth State Listener for real-time auth state updates.
     * This listener monitors authentication state and handles token expiration.
     *
     * When a user is signed in, it verifies and refreshes the ID token to ensure
     * it's valid. This handles edge cases like network issues or manual token invalidation.
     */
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            // Verify token validity and refresh if needed
            user.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // TODO 3.2.1: Extract the token from task.result?.token
                    val token = task.result?.token
                    // Token is valid and refreshed
                    _authState.value = AuthState.Authenticated(
                        userId = user.uid,
                        email = user.email ?: "",
                        token = token
                    )
                } else {
                    // Token refresh failed, force re-authentication
                    auth.signOut()
                    _authState.value = AuthState.Error("Session expired. Please log in again.")
                }
            }
        } else {
            // User is signed out
            if (_authState.value !is AuthState.Loading) {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    init {
        // Register auth state listener for automatic session monitoring
        auth.addAuthStateListener(authStateListener)
    }

    /**
     * TODO 2.2: Checks the current authentication status.
     * Useful for auto-login on app start.
     * This method checks if a user is already signed in with Firebase.
     */
    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // TODO 3.2.2: Retrieve the ID token and include it in the Authenticated state
            // This enhances auto-login by also retrieving the auth token
            currentUser.getIdToken(false).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    _authState.value = AuthState.Authenticated(
                        userId = currentUser.uid,
                        email = currentUser.email ?: "",
                        token = token
                    )
                } else {
                    _authState.value = AuthState.Authenticated(
                        userId = currentUser.uid,
                        email = currentUser.email ?: "",
                        token = null
                    )
                }
            }
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * TODO 1.2: Attempts to sign in with email and password.
     * @param email User's email address
     * @param password User's password
     */
    fun login(email: String, password: String) {
        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Invalid email format")
            return
        }

        // Validate password
        if (password.isEmpty()) {
            _authState.value = AuthState.Error("Password cannot be empty")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        // Set loading state
        _authState.value = AuthState.Loading

        // Attempt Firebase sign-in
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // TODO 3.2.3: Retrieve the ID token after successful login
                    // This ensures the auth token is available for storage
                    user?.getIdToken(false)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val token = tokenTask.result?.token
                            _authState.value = AuthState.Authenticated(
                                userId = user.uid,
                                email = user.email ?: "",
                                token = token
                            )
                        } else {
                            _authState.value = AuthState.Authenticated(
                                userId = user.uid,
                                email = user.email ?: "",
                                token = null
                            )
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(
                        task.exception?.message ?: "Login failed"
                    )
                }
            }
    }

    /**
     * TODO 1.3: Creates a new user account with email and password.
     * @param email User's email address
     * @param password User's password
     */
    fun signUp(email: String, password: String) {
        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Invalid email format")
            return
        }

        // Validate password
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        // Set loading state
        _authState.value = AuthState.Loading

        // Create Firebase user account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // TODO 3.2.4: Retrieve the ID token after successful sign up
                    // This ensures the auth token is available for storage
                    user?.getIdToken(false)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val token = tokenTask.result?.token
                            _authState.value = AuthState.Authenticated(
                                userId = user.uid,
                                email = user.email ?: "",
                                token = token
                            )
                        } else {
                            _authState.value = AuthState.Authenticated(
                                userId = user.uid,
                                email = user.email ?: "",
                                token = null
                            )
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(
                        task.exception?.message ?: "Sign up failed"
                    )
                }
            }
    }

    /**
     * Signs out the current user.
     * Clears Firebase session and updates state to Unauthenticated.
     */
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    override fun onCleared() {
        super.onCleared()
        // Remove auth state listener to prevent memory leaks
        auth.removeAuthStateListener(authStateListener)
    }
}