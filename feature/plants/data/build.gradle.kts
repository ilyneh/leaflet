plugins {
    alias(libs.plugins.leaflet.android.library)
    alias(libs.plugins.leaflet.kotlin.serialization)
}

android {
    namespace = "com.ilynehdev.feature.plants.data"
}

dependencies {
    implementation(projects.core.phloem)
    api(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.network.plants)
    implementation(projects.core.time)

    api(libs.androidx.paging.common)
    api(libs.koin.core)

    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.androidx.room.runtime)
}
