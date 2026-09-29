plugins {
    alias(libs.plugins.leaflet.android.library)
}

android {
    namespace = "com.ilynehdev.data.common"
}

dependencies {
    api(projects.core.phloem)
    implementation(projects.core.database)
    implementation(projects.core.time)

    api(libs.koin.core)
    implementation(libs.androidx.room.runtime)
}
