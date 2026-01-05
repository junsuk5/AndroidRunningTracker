package com.survivalcoding.runningtracker.core.di

import com.survivalcoding.runningtracker.BuildConfig
import com.survivalcoding.runningtracker.presentation.component.LogMapRenderer
import com.survivalcoding.runningtracker.presentation.component.MapRenderer
import com.survivalcoding.runningtracker.presentation.component.NaverMapRenderer
import org.koin.dsl.module

val mapModule = module {
    single<MapRenderer> {
        if (BuildConfig.FLAVOR.contains("dev")) LogMapRenderer()
        else NaverMapRenderer()
    }
}
