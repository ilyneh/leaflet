plugins {
    id("leaflet.android.library")
    id("leaflet.kotlin.serialization")
    id("leaflet.ktor")
}

dependencies {
    api(libs.koin.android)
    testImplementation(libs.junit)
}