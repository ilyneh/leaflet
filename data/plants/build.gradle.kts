plugins {
    alias(libs.plugins.leaflet.android.library)
}

android {
    namespace = "com.ilynehdev.data.plants"
}

dependencies {
    api(projects.core.phloem)
    api(projects.data.common)
    implementation(projects.core.database)
    implementation(projects.core.network.plants)

    implementation(libs.koin.core)
    implementation(libs.androidx.room.paging)

    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.koin.test)
    testImplementation(projects.core.network.client)
}