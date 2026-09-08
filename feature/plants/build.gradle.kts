plugins {
    alias(libs.plugins.leaflet.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ilynehdev.feature.plants"
    buildFeatures {
        compose = true
    }
    androidResources {
        enable = true
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.data.plants)

    // compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.compose)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.kotlinx.coroutines.android)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
