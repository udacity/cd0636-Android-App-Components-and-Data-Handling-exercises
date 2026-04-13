package com.udacity.project.app

/**
 * Sealed class representing sync operation states.
 * Used by the ViewModel to communicate sync status to the UI.
 */
sealed class SyncState {
    object Idle : SyncState()
    object Loading : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
}