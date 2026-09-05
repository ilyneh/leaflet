plugins {
    `kotlin-dsl`
}

group = "com.ilynehdev.leaflet.buildlogic"

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.android.tools.common)
    implementation(libs.ksp.gradle.plugin)

    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.serialization.gradle.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}