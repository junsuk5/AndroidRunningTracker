package com.survivalcoding.runningtracker.presentation

import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.domain.model.SortType

sealed interface MainAction {
    data object ToggleRun : MainAction
    data object FinishRun : MainAction
    data class DeleteRun(val run: Run) : MainAction
    data class ChangeSortType(val sortType: SortType) : MainAction
    data class SelectRun(val run: Run) : MainAction
    data object ToggleGpsStatus : MainAction
    data object RefreshGpsStatus : MainAction
}