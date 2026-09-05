plugins {
    id("leaflet.jvm.library")
    id("leaflet.kotlin.serialization")
    id("leaflet.ktor")
    id("leaflet.contract.test")
}

dependencies {
    implementation(projects.core.network.client)

    implementation(libs.koin.core)
    testImplementation(libs.junit)
}