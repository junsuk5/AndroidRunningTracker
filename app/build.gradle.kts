import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.survivalcoding.runningtracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.survivalcoding.runningtracker"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions += "environment"
    flavorDimensions += "sdk"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "BASE_URL", "\"https://dev.api.runningtracker.com\"")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "BASE_URL", "\"https://staging.api.runningtracker.com\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://api.runningtracker.com\"")
        }

        create("google") {
            dimension = "sdk"
            buildConfigField("String", "MAP_TYPE", "\"google\"")
            manifestPlaceholders["MAP_KEY_NAME"] = "com.google.android.geo.API_KEY"
            manifestPlaceholders["MAP_KEY_VALUE"] = localProperties.getProperty("GOOGLE_MAPS_KEY") ?: ""
        }
        create("naver") {
            dimension = "sdk"
            buildConfigField("String", "MAP_TYPE", "\"naver\"")
            manifestPlaceholders["MAP_KEY_NAME"] = "com.naver.maps.map.NCP_KEY_ID"
            manifestPlaceholders["MAP_KEY_VALUE"] = localProperties.getProperty("NAVER_CLIENT_ID") ?: ""
        }
    }
}

dependencies {
    // Google Maps dependencies (Only for google flavor)
//    "googleImplementation"(libs.play.services.maps)
//    "googleImplementation"("com.google.maps.android:maps-compose:6.4.0")

    // Naver Maps dependencies (Only for naver flavor)
    "naverImplementation"("com.naver.maps:map-sdk:3.23.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}