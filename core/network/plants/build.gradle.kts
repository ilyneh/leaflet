plugins {
    id("leaflet.jvm.library")
    id("leaflet.kotlin.serialization")
    id("leaflet.ktor")
}

dependencies {
    implementation(projects.core.network.client)

    implementation(libs.koin.core)
    testImplementation(libs.junit)
}