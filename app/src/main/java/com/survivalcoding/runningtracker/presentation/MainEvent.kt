package com.survivalcoding.runningtracker.presentation

sealed interface MainEvent {
    data class ShowSnackbar(val message: String) : MainEvent
    data object RunSaved : MainEvent
    data class PermissionRequired(val permission: String) : MainEvent
}
