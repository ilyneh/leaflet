plugins {
    alias(libs.plugins.leaflet.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ilynehdev.core.designsystem"
    buildFeatures { compose = true }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
