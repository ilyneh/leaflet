plugins {
    alias(libs.plugins.leaflet.android.library)
}

android {
    namespace = "com.ilynehdev.data.plants"
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.network.plants)
    implementation(projects.core.phloem)
    implementation(projects.data.common)

    implementation(libs.androidx.room.paging)

    testImplementation(libs.androidx.paging.testing)
}