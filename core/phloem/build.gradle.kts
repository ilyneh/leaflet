plugins {
    alias(libs.plugins.leaflet.jvm.library)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ktor.client.core)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.client.mock)
}
