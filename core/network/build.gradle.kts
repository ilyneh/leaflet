plugins {
    id("leaflet.android.library")
    id("leaflet.kotlin.serialization")
    id("leaflet.ktor")
}

android {
    namespace = "com.ilynehdev.core.network"
}

dependencies {
    api(libs.koin.android)
    testImplementation(libs.junit)
}