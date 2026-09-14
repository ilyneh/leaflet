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
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.compose)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // need for coil
    implementation(libs.ktor.client.android)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor)
}
