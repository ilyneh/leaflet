plugins {
    alias(libs.plugins.leaflet.android.library)
}

android {
    namespace = "com.ilynehdev.data.plants"
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.network.plants)
}