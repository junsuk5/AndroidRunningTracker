package com.survivalcoding.runningtracker.core.di

import com.survivalcoding.runningtracker.domain.use_case.DeleteRunUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetRunsSortedByAvgSpeedUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetRunsSortedByCaloriesBurnedUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetRunsSortedByDateUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetRunsSortedByDistanceUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetRunsSortedByTimeInMillisUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetTotalStatsUseCase
import com.survivalcoding.runningtracker.domain.use_case.SaveRunUseCase
import com.survivalcoding.runningtracker.presentation.MainViewModel
import com.survivalcoding.runningtracker.presentation.service.TrackingManager
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Manager
    single { TrackingManager() }

    // Use Cases
    factory { SaveRunUseCase(get()) }
    factory { GetRunsSortedByDateUseCase(get()) }
    factory { GetRunsSortedByDistanceUseCase(get()) }
    factory { GetRunsSortedByTimeInMillisUseCase(get()) }
    factory { GetRunsSortedByAvgSpeedUseCase(get()) }
    factory { GetRunsSortedByCaloriesBurnedUseCase(get()) }
    factory { GetTotalStatsUseCase(get()) }
    factory { DeleteRunUseCase(get()) }

    // ViewModel
    viewModel {
        MainViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
}
