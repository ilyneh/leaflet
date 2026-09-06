plugins {
    alias(libs.plugins.leaflet.android.library)
    id("leaflet.kotlin.serialization")
    alias(libs.plugins.ksp.gradle.plugin)
    alias(libs.plugins.androidx.room.gradle.plugin)
}

android {
    namespace = "com.ilynehdev.core.database"
    room3 {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)

    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.espresso.core)
}