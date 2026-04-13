package com.udacity.project.app

/**
 * Sealed class representing the state of task synchronization.
 * This provides a type-safe way to represent different sync states.
 */
sealed class SyncState {
    /**
     * Synchronization is in progress.
     */
    object Loading : SyncState()

    /**
     * Synchronization completed successfully.
     */
    object Success : SyncState()

    /**
     * Synchronization was cancelled by the user.
     */
    object Cancelled : SyncState()

    /**
     * Synchronization failed with an error.
     * @param message Error message describing what went wrong
     */
    data class Error(val message: String) : SyncState()

    /**
     * Initial state before any sync operation.
     */
    object Idle : SyncState()
}