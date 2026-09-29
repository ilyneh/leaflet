plugins {
    id("leaflet.jvm.library")
    id("leaflet.kotlin.serialization")
    id("leaflet.ktor")
}

dependencies {
    api(libs.koin.core)
    api(libs.ktor.client.core)
    api(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}