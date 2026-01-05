package com.survivalcoding.runningtracker.core.di

import com.survivalcoding.runningtracker.BuildConfig
import com.survivalcoding.runningtracker.data.location.MockLocationClient
import com.survivalcoding.runningtracker.domain.location.LocationClient
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import com.survivalcoding.runningtracker.presentation.GoogleMapRenderer
import com.survivalcoding.runningtracker.presentation.LogMapRenderer
import com.survivalcoding.runningtracker.presentation.MapRenderer
import com.survivalcoding.runningtracker.presentation.NaverMapRenderer
import com.survivalcoding.runningtracker.presentation.NopMapRenderer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.dsl.module

val flavorModule = module {
    single<LocationClient> {
        if (BuildConfig.FLAVOR.contains("dev") || BuildConfig.FLAVOR.contains("staging")) {
            MockLocationClient()
        } else {
            object : LocationClient {
                override fun getLocationUpdates(interval: Long): Flow<LocationPoint> = emptyFlow()
            }
        }
    }

    single<MapRenderer> {
        if (BuildConfig.FLAVOR.contains("dev")) {
            LogMapRenderer()
        } else {
            when (BuildConfig.MAP_TYPE) {
                "google" -> GoogleMapRenderer()
                "naver" -> NaverMapRenderer()
                else -> NopMapRenderer()
            }
        }
    }
}
