package com.udacity.project.app

// TODO: Step 1 - Create SyncState sealed class
// Create a sealed class to represent different sync states:
// - Loading: Sync operation in progress
// - Success: Sync completed successfully
// - Error(message: String): Sync failed with error message
// - Cancelled: Sync was cancelled by user
// - Idle: No sync operation in progress

/**
 * Sealed class representing the state of task synchronization.
 */
sealed class SyncState {
    object Idle : SyncState()
    object Loading : SyncState()
    object Success : SyncState()
    object Cancelled : SyncState()
    data class Error(val message: String) : SyncState()
}