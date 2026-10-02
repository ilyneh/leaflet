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
    implementation(projects.core.data)
    implementation(projects.feature.plants.data)

    // feature
    implementation(projects.feature.plants.presentation)

    // androidContext(...) when starting the graph under Robolectric
    testImplementation(libs.koin.android)
}

