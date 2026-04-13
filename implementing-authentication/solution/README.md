# Exercise 13 Solution: Firebase Authentication with Secure Storage

This solution demonstrates production-ready authentication implementation using Firebase Authentication, DataStore, and Tink encryption.

## Solution Overview

The solution implements:
- **Firebase Authentication** for email/password login and sign-up
- **Sealed class-based state management** with `AuthState`
- **DataStore + Tink encryption** for secure token storage
- **Auth-aware navigation** that protects task screens
- **Token expiration handling** with automatic refresh
- **Backstack management** to prevent returning to login after authentication

## Architecture Components

### 1. AuthState (Sealed Class)

```kotlin
sealed class AuthState {
    data class Authenticated(
        val userId: String,
        val email: String,
        val token: String? = null
    ) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    object SessionExpired : AuthState()
}
```

**Why this design:**
- Type-safe state representation
- Exhaustive when expressions
- Token is optional to handle async retrieval

### 2. SecurePreferences (DataStore + Tink)

```kotlin
class SecurePreferences(private val context: Context) {

    // DataStore instance
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "secure_auth_prefs"
    )

    // Tink AEAD encryption
    private val aead: Aead by lazy {
        AeadConfig.register()
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, "secure_keyset", "secure_auth_prefs")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://secure_master_key")
            .build()
            .keysetHandle
        keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    // Save methods
    suspend fun saveUserEmail(email: String) {
        val encryptedEmail = encrypt(email)
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_EMAIL] = encryptedEmail
        }
    }

    suspend fun saveAuthToken(token: String) {
        val encryptedToken = encrypt(token)
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = encryptedToken
        }
    }

    // Retrieval methods with logging
    suspend fun getUserEmail(): String? {
        val encryptedEmail = context.dataStore.data.map { preferences ->
            preferences[KEY_USER_EMAIL]
        }.first()

        return encryptedEmail?.let { encrypted ->
            Log.d(TAG, "Encrypted email (Base64): $encrypted")
            decrypt(encrypted)
        }
    }

    suspend fun getAuthToken(): String? {
        val encryptedToken = context.dataStore.data.map { preferences ->
            preferences[KEY_AUTH_TOKEN]
        }.first()

        return encryptedToken?.let { encrypted ->
            Log.d(TAG, "Encrypted token (Base64): ${encrypted.take(50)}...")
            decrypt(encrypted)
        }
    }

    // Encryption/Decryption helpers
    private fun encrypt(plaintext: String): String {
        val encrypted = aead.encrypt(
            plaintext.toByteArray(Charsets.UTF_8),
            ASSOCIATED_DATA.toByteArray(Charsets.UTF_8)
        )
        return android.util.Base64.encodeToString(encrypted, android.util.Base64.NO_WRAP)
    }

    private fun decrypt(encryptedBase64: String): String {
        val encryptedBytes = android.util.Base64.decode(encryptedBase64, android.util.Base64.NO_WRAP)
        val decrypted = aead.decrypt(
            encryptedBytes,
            ASSOCIATED_DATA.toByteArray(Charsets.UTF_8)
        )
        val decryptedString = String(decrypted, Charsets.UTF_8)
        Log.d(TAG, "Successfully decrypted data")
        return decryptedString
    }
}
```

**Key features:**
- **DataStore** for reactive, async storage (replaces SharedPreferences)
- **Tink AEAD** encryption with Android Keystore backing
- **Logging** of encrypted values for debugging
- **Suspend functions** for coroutine-based async operations

### 3. AuthViewModel (State Management)

```kotlin
class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _authState = MutableLiveData<AuthState>(AuthState.Unauthenticated)
    val authState: LiveData<AuthState> = _authState

    // Auth state listener for token expiration monitoring
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

    // Login with token retrieval
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

    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
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

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}
```

**Key features:**
- **Auth state listener** monitors Firebase session and token validity
- **Token retrieval** in login, sign-up, and checkAuthStatus
- **Automatic token refresh** with `getIdToken(true)`
- **Session expiration** detection and handling

### 4. MainActivity (Navigation)

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Task list is a top-level destination (no back button)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.taskListFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun observeAuthState() {
        authViewModel.authState.observe(this) { state ->
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            when (state) {
                is AuthState.Authenticated -> {
                    if (navController.currentDestination?.id == R.id.loginFragment) {
                        navController.navigate(R.id.action_loginFragment_to_taskListFragment)
                    }
                    invalidateOptionsMenu()
                }
                is AuthState.Unauthenticated, is AuthState.SessionExpired -> {
                    if (navController.currentDestination?.id != R.id.loginFragment) {
                        // Clear backstack and navigate to login as new root
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.taskListFragment, inclusive = true)
                            .build()
                        navController.navigate(R.id.loginFragment, null, navOptions)
                    }
                    invalidateOptionsMenu()
                }
                else -> {
                    // Loading or Error states handled by LoginFragment
                }
            }
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                lifecycleScope.launch {
                    try {
                        securePrefs.clear()
                    } catch (e: Exception) {
                        // Continue with logout even if clearing preferences fails
                    }
                    authViewModel.logout()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
```

**Key features:**
- **AppBarConfiguration** removes back button from task screens
- **NavOptions** clears backstack on logout
- **Secure storage cleanup** before logout
- **State-based navigation** ensures correct screen based on auth state

### 5. LoginFragment (Token Storage)

```kotlin
private fun observeAuthState() {
    authViewModel.authState.observe(viewLifecycleOwner) { state ->
        when (state) {
            is AuthState.Authenticated -> {
                // Save email and token to secure storage
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        securePrefs.saveUserEmail(state.email)
                        state.token?.let { token ->
                            securePrefs.saveAuthToken(token)
                        }
                    } catch (e: Exception) {
                        // Continue even if secure storage fails
                    }
                }

                binding.loadingProgressBar.visibility = View.GONE
            }
            // ... other states
        }
    }
}
```

**Key features:**
- **Coroutine scope** tied to view lifecycle
- **Token storage** when available
- **Graceful error handling** doesn't block authentication

## Security Considerations

### 1. Encryption
- **Tink AEAD** provides authenticated encryption with associated data
- **Android Keystore** backs the master key for hardware-level security
- **Base64 encoding** for safe string storage in DataStore

### 2. Token Management
- **ID tokens** refreshed automatically by Firebase (hourly)
- **Manual refresh** with `getIdToken(true)` in auth state listener
- **Session expiration** handled gracefully with re-authentication prompt

### 3. Data Storage
- **No password storage** - Firebase handles authentication
- **Encrypted email** for display purposes only
- **Encrypted token** for API calls (if needed)

### 4. Navigation Security
- **Backstack cleared** on logout prevents back button bypass
- **Top-level destination** configuration prevents accidental navigation
- **State-based navigation** ensures correct screen for auth status

## Testing Notes

### Manual Testing
1. **Sign up** with new email - verify account creation
2. **Login** with credentials - verify successful authentication
3. **Auto-login** - close/reopen app, should stay logged in
4. **Logout** - press back button, should exit app (not return to tasks)
5. **Token storage** - check Logcat for encrypted values
6. **Session expiration** - manually invalidate token in Firebase Console

### Logcat Monitoring
Look for these logs:
```
D/SecurePreferences: Encrypted email (Base64): AQICAHh8kX7...
D/SecurePreferences: Successfully decrypted data
D/SecurePreferences: Encrypted token (Base64): AQICAHh8kX7yN...
```

## Common Issues

### 1. Firebase Auth Error
**Problem**: "The email address is badly formatted"
**Solution**: Validate email with `Patterns.EMAIL_ADDRESS`

### 2. Token Not Saved
**Problem**: Token is null in AuthState
**Solution**: Ensure `getIdToken()` is called after successful login

### 3. Back Button Returns to Login
**Problem**: User can press back to return to login
**Solution**: Verify `NavOptions` with `popUpTo` is set correctly

### 4. Encryption Error
**Problem**: "KeysetHandle not initialized"
**Solution**: Ensure Tink is registered with `AeadConfig.register()`

## Best Practices Demonstrated

1. **Separation of concerns** - ViewModel handles auth logic, Fragment handles UI
2. **Reactive programming** - LiveData for state observation
3. **Coroutines** - Async operations with proper lifecycle scoping
4. **Type safety** - Sealed classes for exhaustive state handling
5. **Security** - Modern encryption with hardware-backed keys
6. **User experience** - Loading states, error messages, logout confirmation
7. **Clean code** - Well-documented, modular, testable

## Additional Resources

- [Firebase Authentication Docs](https://firebase.google.com/docs/auth)
- [DataStore Guide](https://developer.android.com/topic/libraries/architecture/datastore)
- [Tink Crypto Library](https://github.com/google/tink)
- [Android Keystore System](https://developer.android.com/training/articles/keystore)