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

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
