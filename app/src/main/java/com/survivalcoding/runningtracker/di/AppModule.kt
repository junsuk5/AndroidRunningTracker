package com.survivalcoding.runningtracker.di

import com.survivalcoding.runningtracker.data.repository.MockRunRepositoryImpl
import com.survivalcoding.runningtracker.domain.repository.RunRepository
import com.survivalcoding.runningtracker.domain.use_case.*
import org.koin.dsl.module

val appModule = module {
    // Repository
    single<RunRepository> { MockRunRepositoryImpl() }

    // Use Cases
    factory { SaveRunUseCase(get()) }
    factory { GetRunsSortedByDateUseCase(get()) }
    factory { GetRunsSortedByDistanceUseCase(get()) }
    factory { GetRunsSortedByTimeInMillisUseCase(get()) }
    factory { GetRunsSortedByAvgSpeedUseCase(get()) }
    factory { GetRunsSortedByCaloriesBurnedUseCase(get()) }
    factory { GetTotalStatsUseCase(get()) }
}
