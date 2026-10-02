plugins {
    alias(libs.plugins.leaflet.android.library)
}

android {
    namespace = "com.ilynehdev.core.data"
}

dependencies {
    implementation(projects.core.phloem)
    implementation(projects.core.database)
    implementation(projects.core.time)

    api(libs.koin.core)
    implementation(libs.androidx.room.runtime)
}
