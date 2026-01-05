package com.survivalcoding.runningtracker.core.di

import androidx.room.Room
import com.survivalcoding.runningtracker.BuildConfig
import com.survivalcoding.runningtracker.data.database.RunDatabase
import com.survivalcoding.runningtracker.data.location.MockLocationClient
import com.survivalcoding.runningtracker.data.location.MockGpsStatusProvider
import com.survivalcoding.runningtracker.data.repository.MockRunRepositoryImpl
import com.survivalcoding.runningtracker.data.repository.RunRepositoryImpl
import com.survivalcoding.runningtracker.domain.location.LocationClient
import com.survivalcoding.runningtracker.domain.location.GpsStatusProvider
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import com.survivalcoding.runningtracker.domain.model.GpsStatus
import com.survivalcoding.runningtracker.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.koin.dsl.module

val flavorModule = module {
    // Repository & Database
    if (BuildConfig.FLAVOR.contains("dev")) {
        single<RunRepository> { MockRunRepositoryImpl() }
    } else {
        single {
            if (BuildConfig.FLAVOR.contains("staging")) {
                Room.inMemoryDatabaseBuilder(get(), RunDatabase::class.java).build()
            } else {
                Room.databaseBuilder(get(), RunDatabase::class.java, "running_db").build()
            }
        }
        single { get<RunDatabase>().getRunDao() }
        single<RunRepository> { RunRepositoryImpl(get()) }
    }

    // GPS Status
    single<GpsStatusProvider> {
        if (BuildConfig.FLAVOR.contains("dev") || BuildConfig.FLAVOR.contains("staging")) {
            MockGpsStatusProvider()
        } else {
            object : GpsStatusProvider {
                override fun observeGpsStatus(): Flow<GpsStatus> = flowOf(GpsStatus.Acquired)
            }
        }
    }

    // Location
    single<LocationClient> {
        if (BuildConfig.FLAVOR.contains("dev") || BuildConfig.FLAVOR.contains("staging")) {
            MockLocationClient()
        } else {
            object : LocationClient {
                override fun getLocationUpdates(interval: Long): Flow<LocationPoint> = emptyFlow()
            }
        }
    }
}
