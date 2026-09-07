plugins {
    alias(libs.plugins.leaflet.android.library)
}

android {
    namespace = "com.ilynehdev.data.common"
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.phloem)

    implementation(libs.koin.core)
    implementation(libs.androidx.room.runtime)
}
