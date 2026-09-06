plugins {
    alias(libs.plugins.leaflet.android.library)
    id("leaflet.kotlin.serialization")
    alias(libs.plugins.ksp.gradle.plugin)
}

android {
    namespace = "com.ilynehdev.core.database"
}

dependencies {
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)

    androidTestImplementation(libs.androidx.espresso.core)
}