plugins {
    alias(libs.plugins.leaflet.android.library)
    alias(libs.plugins.leaflet.android.room)
    alias(libs.plugins.leaflet.kotlin.serialization)
}

android {
    namespace = "com.ilynehdev.core.database"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.paging)

    api(libs.koin.core)
    implementation(libs.koin.android)
}