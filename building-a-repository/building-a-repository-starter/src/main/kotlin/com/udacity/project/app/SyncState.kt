package com.udacity.project.app

/**
 * Sealed class representing the state of sync operations.
 * Used to communicate loading, success, and error states to the UI.
 */
sealed class SyncState {
    /**
     * Idle state - no sync operation in progress.
     */
    object Idle : SyncState()

    /**
     * Loading state - sync operation in progress.
     */
    object Loading : SyncState()

    /**
     * Success state - sync operation completed successfully.
     */
    object Success : SyncState()

    /**
     * Error state - sync operation failed.
     * @param message The error message describing what went wrong.
     */
    data class Error(val message: String) : SyncState()
}