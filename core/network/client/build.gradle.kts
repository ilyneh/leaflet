plugins {
    id("leaflet.jvm.library")
    id("leaflet.kotlin.serialization")
    id("leaflet.ktor")
}

dependencies {
    implementation(libs.koin.core)
    testImplementation(libs.junit)
}