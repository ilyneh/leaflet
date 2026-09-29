plugins {
    alias(libs.plugins.leaflet.android.library)
}

android {
    namespace = "com.ilynehdev.data.plants"
}

dependencies {
    implementation(projects.core.phloem)
    api(projects.data.common)
    implementation(projects.core.database)
    implementation(projects.core.network.plants)
    implementation(projects.core.time)

    api(libs.androidx.paging.common)
    api(libs.koin.core)

    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.androidx.room.runtime)
}
