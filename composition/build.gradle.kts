plugins {
    alias(libs.plugins.leaflet.android.library)
}

android {
    namespace = "com.ilynehdev.composition"
}

dependencies {
    api(libs.koin.core)

    // core
    implementation(projects.core.database)
    implementation(projects.core.network.client)
    implementation(projects.core.network.plants)
    implementation(projects.core.time)

    // data
    implementation(projects.data.common)
    implementation(projects.data.plants)

    // feature
    implementation(projects.feature.plants)

    // androidContext(...) when starting the graph under Robolectric
    testImplementation(libs.koin.android)
}

