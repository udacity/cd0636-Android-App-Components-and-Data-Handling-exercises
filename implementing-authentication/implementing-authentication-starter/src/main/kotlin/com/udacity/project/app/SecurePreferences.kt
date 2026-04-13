package com.udacity.project.app

import android.content.Context
import android.util.Log

/**
 * TODO 3.1: Implement secure storage using DataStore and Tink encryption.
 *
 * This class provides encrypted storage for sensitive user data (email and auth tokens).
 * Follow the sub-steps below to complete the implementation.
 *
 * Requirements:
 * - Use DataStore Preferences for reactive storage
 * - Use Tink AEAD encryption with Android Keystore
 * - Implement suspend functions for async operations
 * - Store user email and auth token securely (encrypted)
 *
 * Note: Passwords should NOT be stored here - Firebase handles password authentication.
 */
class SecurePreferences(private val context: Context) {

    // TODO 3.1.1: Initialize DataStore with preferencesDataStore delegate
    // private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    //     name = "secure_auth_prefs"
    // )

    // TODO 3.1.2: Setup Tink AEAD encryption using AndroidKeysetManager
    // private val aead: Aead by lazy {
    //     AeadConfig.register()
    //     val keysetHandle = AndroidKeysetManager.Builder()
    //         .withSharedPref(context, "secure_keyset", "secure_auth_prefs")
    //         .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
    //         .withMasterKeyUri("android-keystore://secure_master_key")
    //         .build()
    //         .keysetHandle
    //     keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    // }

    // TODO 3.1.3: Add preference keys
    // private companion object {
    //     private const val TAG = "SecurePreferences"
    //     val KEY_USER_EMAIL = stringPreferencesKey("user_email_encrypted")
    //     val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token_encrypted")
    //     private const val ASSOCIATED_DATA = "auth_data"
    // }

    /**
     * TODO 3.1.4: Implement saveUserEmail() method
     * Save user email (encrypted using Tink)
     * This should:
     * 1. Encrypt the email using the encrypt() helper
     * 2. Store the encrypted value in DataStore using the KEY_USER_EMAIL key
     */
    suspend fun saveUserEmail(email: String) {
        // TODO: Implement email encryption and storage
        // val encryptedEmail = encrypt(email)
        // context.dataStore.edit { preferences ->
        //     preferences[KEY_USER_EMAIL] = encryptedEmail
        // }
    }

    /**
     * TODO 3.1.5: Implement saveAuthToken() method
     * Save authentication token (encrypted using Tink)
     * This should:
     * 1. Encrypt the token using the encrypt() helper
     * 2. Store the encrypted value in DataStore using the KEY_AUTH_TOKEN key
     */
    suspend fun saveAuthToken(token: String) {
        // TODO: Implement token encryption and storage
        // val encryptedToken = encrypt(token)
        // context.dataStore.edit { preferences ->
        //     preferences[KEY_AUTH_TOKEN] = encryptedToken
        // }
    }

    /**
     * TODO 3.1.6: Implement clear() method
     * Clear all stored data
     * This should clear all preferences from DataStore
     */
    suspend fun clear() {
        // TODO: Implement clear functionality
        // context.dataStore.edit { preferences ->
        //     preferences.clear()
        // }
    }

    /**
     * TODO 3.1.7: Add private encrypt() helper function
     * Encrypt a string using Tink AEAD
     * This should:
     * 1. Convert plaintext to bytes
     * 2. Encrypt using aead.encrypt() with ASSOCIATED_DATA
     * 3. Encode result as Base64 string
     */
    // private fun encrypt(plaintext: String): String {
    //     val encrypted = aead.encrypt(
    //         plaintext.toByteArray(Charsets.UTF_8),
    //         ASSOCIATED_DATA.toByteArray(Charsets.UTF_8)
    //     )
    //     return android.util.Base64.encodeToString(encrypted, android.util.Base64.NO_WRAP)
    // }

    /**
     * BONUS (Optional): Implement retrieval methods if you want to use stored data
     * These are not required for basic authentication flow but useful for:
     * - Using the stored token for API calls
     * - Debugging encryption/decryption
     * - Verifying stored data
     */

    /**
     * BONUS 3.1.8: Implement getUserEmail() method
     * Retrieve user email (decrypted)
     * This should:
     * 1. Read the encrypted email from DataStore
     * 2. Log the encrypted value (Base64) before decrypting
     * 3. Decrypt using Tink AEAD
     * 4. Return the decrypted email string or null if not found
     */
    suspend fun getUserEmail(): String? {
        // TODO: Implement email retrieval and decryption
        // val encryptedEmail = context.dataStore.data.map { preferences ->
        //     preferences[KEY_USER_EMAIL]
        // }.first()
        // return encryptedEmail?.let { encrypted ->
        //     Log.d(TAG, "Encrypted email (Base64): $encrypted")
        //     decrypt(encrypted)
        // }
        return null
    }

    /**
     * BONUS 3.1.9: Implement getAuthToken() method
     * Retrieve authentication token (decrypted)
     * This should:
     * 1. Read the encrypted token from DataStore
     * 2. Log the encrypted value (Base64, first 50 chars) before decrypting
     * 3. Decrypt using Tink AEAD
     * 4. Return the decrypted token string or null if not found
     */
    suspend fun getAuthToken(): String? {
        // TODO: Implement token retrieval and decryption
        // val encryptedToken = context.dataStore.data.map { preferences ->
        //     preferences[KEY_AUTH_TOKEN]
        // }.first()
        // return encryptedToken?.let { encrypted ->
        //     Log.d(TAG, "Encrypted token (Base64): ${encrypted.take(50)}...")
        //     decrypt(encrypted)
        // }
        return null
    }

    /**
     * BONUS 3.1.10: Add private decrypt() helper function
     * Decrypt a string using Tink AEAD
     * This should:
     * 1. Decode Base64 string to bytes
     * 2. Decrypt using aead.decrypt() with ASSOCIATED_DATA
     * 3. Convert result to string
     * 4. Log successful decryption
     */
    // private fun decrypt(encryptedBase64: String): String {
    //     val encryptedBytes = android.util.Base64.decode(encryptedBase64, android.util.Base64.NO_WRAP)
    //     val decrypted = aead.decrypt(
    //         encryptedBytes,
    //         ASSOCIATED_DATA.toByteArray(Charsets.UTF_8)
    //     )
    //     val decryptedString = String(decrypted, Charsets.UTF_8)
    //     Log.d(TAG, "Successfully decrypted data")
    //     return decryptedString
    // }
}