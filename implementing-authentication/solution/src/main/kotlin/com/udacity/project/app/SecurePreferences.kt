package com.udacity.project.app

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Secure preferences using DataStore and Tink for encryption.
 * Replaces deprecated EncryptedSharedPreferences with modern alternatives.
 */
class SecurePreferences(private val context: Context) {

    // DataStore instance for secure storage
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "secure_auth_prefs"
    )

    // Tink encryption/decryption
    private val aead: Aead by lazy {
        // Initialize Tink
        AeadConfig.register()

        // Create or retrieve Android Keystore-backed keyset
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, "secure_keyset", "secure_auth_prefs")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://secure_master_key")
            .build()
            .keysetHandle

        keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    // Preference keys
    private companion object {
        private const val TAG = "SecurePreferences"
        val KEY_USER_EMAIL = stringPreferencesKey("user_email_encrypted")
        val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token_encrypted")
        private const val ASSOCIATED_DATA = "auth_data"
    }

    /**
     * Save user email (encrypted using Tink)
     */
    suspend fun saveUserEmail(email: String) {
        val encryptedEmail = encrypt(email)
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_EMAIL] = encryptedEmail
        }
    }

    /**
     * Save authentication token (encrypted using Tink)
     */
    suspend fun saveAuthToken(token: String) {
        val encryptedToken = encrypt(token)
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = encryptedToken
        }
    }

    /**
     * Retrieve user email (decrypted)
     */
    suspend fun getUserEmail(): String? {
        val encryptedEmail = context.dataStore.data.map { preferences ->
            preferences[KEY_USER_EMAIL]
        }.first()

        return encryptedEmail?.let { encrypted ->
            Log.d(TAG, "Encrypted email (Base64): $encrypted")
            decrypt(encrypted)
        }
    }

    /**
     * Retrieve authentication token (decrypted)
     */
    suspend fun getAuthToken(): String? {
        val encryptedToken = context.dataStore.data.map { preferences ->
            preferences[KEY_AUTH_TOKEN]
        }.first()

        return encryptedToken?.let { encrypted ->
            Log.d(TAG, "Encrypted token (Base64): ${encrypted.take(50)}...") // Log first 50 chars
            decrypt(encrypted)
        }
    }

    /**
     * Clear all stored data
     */
    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Encrypt a string using Tink AEAD
     */
    private fun encrypt(plaintext: String): String {
        val encrypted = aead.encrypt(
            plaintext.toByteArray(Charsets.UTF_8),
            ASSOCIATED_DATA.toByteArray(Charsets.UTF_8)
        )
        return android.util.Base64.encodeToString(encrypted, android.util.Base64.NO_WRAP)
    }

    /**
     * Decrypt a string using Tink AEAD
     */
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