package com.udacity.project.app

sealed class SyncState {
    object Idle : SyncState()
    object Loading : SyncState()
    object Success : SyncState()
    object Cancelled : SyncState()
    data class Error(val message: String) : SyncState()
}
