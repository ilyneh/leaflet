plugins {
    id("leaflet.android.library")
    id("leaflet.kotlin.serialization")
    id("leaflet.ktor")
}

android {
    namespace = "com.ilynehdev.core.network"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }
    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(libs.koin.android)
    testImplementation(libs.junit)
}