plugins {
    alias(libs.plugins.leaflet.jvm.library)
}

dependencies {
    api(projects.core.time)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ktor.client.core)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.client.mock)
}
