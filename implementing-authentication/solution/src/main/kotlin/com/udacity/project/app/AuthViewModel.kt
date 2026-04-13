package com.udacity.project.app

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private val _authState = MutableLiveData<AuthState>(AuthState.Unauthenticated)
    val authState: LiveData<AuthState> = _authState

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    _authState.value = AuthState.Authenticated(
                        userId = user.uid,
                        email = user.email ?: "",
                        token = token
                    )
                } else {
                    auth.signOut()
                    _authState.value = AuthState.SessionExpired
                }
            }
        } else {
            if (_authState.value !is AuthState.Error) {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    fun login(email: String, password: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Invalid email format")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Retrieve the ID token
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

    fun signUp(email: String, password: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Invalid email format")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Retrieve the ID token
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

    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Retrieve the ID token
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

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun resetErrorState() {
        if (_authState.value is AuthState.Error || _authState.value is AuthState.SessionExpired) {
            _authState.value = AuthState.Unauthenticated
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}