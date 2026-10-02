plugins {
    alias(libs.plugins.leaflet.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ilynehdev.feature.plants.presentation"
    buildFeatures {
        compose = true
    }
    androidResources {
        enable = true
    }
}

dependencies {
    implementation(projects.core.designsystem)
    api(projects.feature.plants.data)

    // compose
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.koin.test)
    testImplementation(projects.core.network.client)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    api(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.compose)

    implementation(libs.kotlinx.coroutines.android)

    api(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // need for coil
    implementation(libs.ktor.client.android)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor)
}
